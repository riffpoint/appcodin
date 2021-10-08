//
//  ViewController.swift
//
//  Created by info@appcodin.com
//

import UIKit
import CoreData
import Network

class UserListViewController: UIViewController {

    //MARK: - Properties
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    var queue: DispatchQueue = DispatchQueue.global(qos: .background)
    let semaphore = DispatchSemaphore(value: 1)
    
    var isStartLoadNextPage: Bool = false
    var selectedIndexPath: IndexPath = IndexPath(row: 0, section: 0)
    var autoloadDataFromLastRequest: Bool = false
    
    let monitor = NWPathMonitor()
    
    // MARK: - Init fetch result controller
    lazy var fetchedhResultController: NSFetchedResultsController<NSFetchRequestResult> = {
        let fetchRequest = NSFetchRequest<NSFetchRequestResult>(entityName: String(describing: UsersList.self))
        fetchRequest.sortDescriptors = [NSSortDescriptor(key: "user_id", ascending: true)]
        let frc = NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: CoreDataStack.sharedInstance.persistentContainer.viewContext, sectionNameKeyPath: nil, cacheName: nil)
        frc.delegate = self
        return frc
    }()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configureUI()
        updateTableContent()
        startMonitoringNetwork()
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(true)
        self.tableView.reloadData()
    }

    
    // MARK: - UI configs
    
    func configureUI() {
        self.navigationItem.title = NSLocalizedString("Users", comment: "")
        self.tableView.rowHeight = 100
    }
    
    
    // MARK: - Action methods
    
    func hideKeyboardWithSearchBar(bar:UISearchBar) {
        bar.resignFirstResponder()
    }

    // MARK: - Fetch data from db and then download from server side
    func updateTableContent() {
        localFetch()
        if(fetchedhResultController.sections?.first?.numberOfObjects == 0) {
            self.showProgressHUD(self.view)
            downloadUsersListFromLastUserID(0)
        }
    }
    
    func localFetch() {
        do {
            try self.fetchedhResultController.performFetch()
        } catch let error  {
            print(">>>>>>>>>> ERROR: \(error)")
        }
    }
    
    // MARK: - API function
    func downloadUsersListFromLastUserID(_ user_id: Int) {
        queue.async {
            self.semaphore.wait()
                
            defer {
                self.semaphore.signal()
            }
            
            NetworkManager.downloadUsersList(pageNumber: user_id, successComplition: { data in
                if let response = try? JSONDecoder().decode([UserListModel].self, from: data) {
                    DataManager.sharedInstance.saveUserList(list: response)
                } else {
                    DispatchQueue.main.async {
                        self.showErrorAlert("Decoding data error")
                    }
                }
                DispatchQueue.main.async {
                    self.closeProgressHUD(self.view)
                }
                
                self.isStartLoadNextPage = false
            } , failureComplition: { error in
                if let nserror = error as NSError? {
                    if nserror.code == -1009 {
                        self.autoloadDataFromLastRequest = true
                    }
                }
                DispatchQueue.main.async {
                    self.closeProgressHUD(self.view)
                    self.showErrorAlert(error.localizedDescription)
                }
                self.isStartLoadNextPage = false
            });
        }
    }
    
    // MARK: - Handling action between controller
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if segue.identifier == "showProfile" {
            let vc = segue.destination as? UserProfileViewController
            if let user = fetchedhResultController.object(at: self.selectedIndexPath) as? UsersList {
                vc?.userEntity = user
            }
        }
    }
    
    // MARK: - Handling Network state
    
    fileprivate func startMonitoringNetwork() {
        monitor.start(queue: DispatchQueue.global(qos: .background))
        
        monitor.pathUpdateHandler = { [self] path in
            if path.status == .satisfied {
                if self.autoloadDataFromLastRequest {
                    let lastUserID = DataManager.sharedInstance.getLastUserID()
                    downloadUsersListFromLastUserID(lastUserID)
                    self.autoloadDataFromLastRequest = false
                }
            } else  if path.status == .unsatisfied {
                print("Noo! We haven't internet!")
            }
        }
    }
    
    fileprivate func storMonitoringNetwork() {
        monitor.cancel()
    }
}


// MARK: - UITableViewSource

extension UserListViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let count = fetchedhResultController.sections?.first?.numberOfObjects {
            return count
        }
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.tableView.dequeueReusableCell(withIdentifier: UserListTableViewCell.cellIdentifier, for: indexPath) as! UserListTableViewCell
        
        if let user = fetchedhResultController.object(at: indexPath) as? UsersList {
            cell.configureCell(user, invertedAvatar: ((indexPath.row+1)%4==0))
        }
        
        return cell
    }
}


// MARK: - UITableViewDelegate methods

extension UserListViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        self.tableView .deselectRow(at: indexPath, animated: true)
        self.selectedIndexPath = indexPath
        self.performSegue(withIdentifier: "showProfile", sender: nil)
        self.view .endEditing(true)
    }
}


// MARK: - NSFetchedResultsControllerDelegate delegate methods

extension UserListViewController: NSFetchedResultsControllerDelegate {
    
    func controller(_ controller: NSFetchedResultsController<NSFetchRequestResult>, didChange anObject: Any, at indexPath: IndexPath?, for type: NSFetchedResultsChangeType, newIndexPath: IndexPath?) {
        
        switch type {
        case .insert:
            self.tableView.insertRows(at: [newIndexPath!], with: .automatic)
        default:
            break
        }
    }
    
    func controllerDidChangeContent(_ controller: NSFetchedResultsController<NSFetchRequestResult>) {
        self.tableView.endUpdates()
    }
    
    func controllerWillChangeContent(_ controller: NSFetchedResultsController<NSFetchRequestResult>) {
        self.tableView.beginUpdates()
    }
}


// MARK: - ScrollViewDelegate methods

extension UserListViewController: UIScrollViewDelegate {
    
    func scrollViewDidScroll(_ scrollView: UIScrollView) {
        self.view.endEditing(true)
    }
    
    /**
     Download the next page with users
     */
    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        if ((tableView.contentOffset.y + tableView.frame.size.height) >= tableView.contentSize.height - 20) && !self.isStartLoadNextPage {
            let lastUserID = DataManager.sharedInstance.getLastUserID()
            downloadUsersListFromLastUserID(lastUserID)
        }
    }
}


// MARK: - UISeachBarDelegate methods

extension UserListViewController: UISearchBarDelegate {
    func searchBarShouldBeginEditing(_ searchBar: UISearchBar) -> Bool {
        return true
    }
    
    func searchBarTextDidEndEditing(_ searchBar: UISearchBar) {
        DispatchQueue.main.async {
            self.fetchedhResultController.fetchRequest.predicate = nil
            self.localFetch()
            self.tableView.reloadData()
        }
    }
    
    func searchBar(_ searchBar: UISearchBar, textDidChange searchText: String) {        
        if searchText.count == 0 {
            hideKeyboardWithSearchBar(bar: searchBar)
        }
        
        DispatchQueue.main.async {
            if searchText.count > 0 {
                let containsPredicate: NSPredicate = NSPredicate(format: "username contains[cd] %@",  searchText)
                let matchPredicate: NSPredicate = NSPredicate(format: "username MATCHES[cd] %@",  searchText)
                let notesPredicate: NSPredicate = NSPredicate(format: "notes contains[cd] %@", searchText)
                let finalPredicate: NSPredicate = NSCompoundPredicate(orPredicateWithSubpredicates: [containsPredicate, matchPredicate, notesPredicate])
                self.fetchedhResultController.fetchRequest.predicate = finalPredicate
                
                self.localFetch()
                self.tableView.reloadData()
            }
        }
    }
}
