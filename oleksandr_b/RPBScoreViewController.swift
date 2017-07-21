//
//  RPBScoreViewController.swift
//  GameProject
//
//  Created by AppCodin <info@appcodin.com> on 5/12/17.
//  Copyright © 2017 AppCodin <info@appcodin.com>. All rights reserved.
//

import UIKit

class RPBScoreViewController: UIViewController {
    // MARK: - Properties
    fileprivate let prizeCellIdentifier = "RPBPrizeCellIdentifier"
    fileprivate let simpleCellIdentifier = "RPBSimpleCellIdentifier"
    
    fileprivate var scoreTable : RPBScoreTable?
    
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var messageImageView: UIImageView!
    
    // MARK: - Life Cicle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        scoreTable = RPBUserDefaults.loadScoreTable()
        
        if scoreTable == nil || scoreTable?.records.count == 0 {
            scoreTable = RPBScoreTable.defaultData()
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    // MARK: - Actions
    @IBAction func backAction(_ sender: UIButton) {
        dismiss(animated: false, completion: nil)
    }    
}

    // MARK: - UITableViewDataSource
extension RPBScoreViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let count = scoreTable?.records.count {
            tableView.isHidden = false
            self.messageImageView.isHidden = count != 0
            
            return count
        }
        
        tableView.isHidden = true
        self.messageImageView.isHidden = false
        
        return 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let userRecord = scoreTable?.records[indexPath.row]
        
        let isPrizePlace = indexPath.row >= 0 && indexPath.row < 3
        
        if isPrizePlace {
            let cell = tableView.dequeueReusableCell(withIdentifier: self.prizeCellIdentifier, for: indexPath) as! RPBPrizeTableViewCell
            cell.backgroundColor = .clear
            cell.nameLabel.text = userRecord?.name
            cell.placeImageView.image = UIImage(named: prizesImageNames[indexPath.row])
            cell.pointsLabel.text = userRecord?.points.description
            
            return cell
        }
        else {
            let cell = tableView.dequeueReusableCell(withIdentifier: self.simpleCellIdentifier, for: indexPath) as! RPBSimpleTableViewCell
            cell.backgroundColor = .clear
            cell.placeLabel.text = String(indexPath.row + 1)
            cell.nameLabel.text = userRecord?.name
            cell.pointsLabel.text = userRecord?.points.description
            
            return cell
        }
    }
}
