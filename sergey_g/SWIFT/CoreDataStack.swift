//
//  CoreDataStack.swift
//
//  Created by info@appcodin.com
//

import Foundation
import CoreData


class CoreDataStack: NSObject {
    
    static let sharedInstance = CoreDataStack()
    fileprivate var persistantStoreLoaded: Bool = false
    
    private override init() {}
    
    
    lazy var persistentContainer: NSPersistentContainer = {
        let container = NSPersistentContainer(name: "Git_App")
        container.loadPersistentStores(completionHandler: { (storeDescription, error) in
            if let error = error as NSError? {
                print("Unresolved error \(error), \(error.userInfo)")
            }
        })
        
        container.viewContext.mergePolicy = NSOverwriteMergePolicy
        container.viewContext.automaticallyMergesChangesFromParent = true
        
        return container
    }()

    
    // MARK: - Core Data Saving support

    func saveContext () {
        let context = persistentContainer.viewContext
        if context.hasChanges {
            do {
                try context.save()
            } catch {
                let nserror = error as NSError
                print("Unresolved error \(nserror), \(nserror.userInfo)")
            }
        }
    }

    func mainContext() -> NSManagedObjectContext? {
        return self.persistentContainer.viewContext
    }
    
    func backgroundContext() -> NSManagedObjectContext? {
        let container = self.persistentContainer
        let context = container.newBackgroundContext()
        context.mergePolicy = NSOverwriteMergePolicy
        context.automaticallyMergesChangesFromParent = true
        return context
    }
    
    func performForBackgroundContext(_ block: @escaping (NSManagedObjectContext) -> Void) {
        self.persistentContainer.performBackgroundTask { context in
            context.mergePolicy = NSOverwriteMergePolicy
            context.automaticallyMergesChangesFromParent = true
            block (context)
        }
    }
}

extension NSManagedObjectContext {
    
    public func saveThrows() {
        if self.hasChanges {
            do {
                try self.save()
            } catch {
                let nserror = error as NSError
                NSLog("CoreDataStack saveThrows Unresolved error \(nserror), \(nserror.userInfo)")
            }
        }
    }
}

extension CoreDataStack {
    func appDirectory() {
        if let url = FileManager.default.urls(for: .libraryDirectory, in: .userDomainMask).last {
            print(url.absoluteString)
        }
    }
}
