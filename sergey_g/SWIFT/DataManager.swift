//
//  DataManager.swift
//
//  Created by info@appcodin.com
//

import Foundation


class DataManager: NSObject {
    static let sharedInstance = DataManager()
    
    func saveUserList(list: [UserListModel]) {
        let context = CoreDataStack.sharedInstance.mainContext()
        _ = list.map{[UsersList.initWithAPIModel(entity: $0, inContext: context!)]}
        CoreDataStack.sharedInstance.saveContext()
    }
    
    func saveProfile(_ profile: UserProfileModel) -> UserProfile {        
        let context = CoreDataStack.sharedInstance.mainContext()
        let profile = UserProfile.initWithAPIModel(entity: profile, inContext: context!)
        CoreDataStack.sharedInstance.saveContext()
        
        return profile
    }
    
    func fetchProfileWithID(user_id: Int) -> UserProfile? {
        let context = CoreDataStack.sharedInstance.mainContext()
        guard let profile = UserProfile.fetchProfileWithID(Int32(user_id), inContext: context!) else {
            return nil
        }
        
        return profile
    }
    
    func getLastUserID() -> Int {
        let context = CoreDataStack.sharedInstance.mainContext()
        guard let lastUser = UsersList.fetchLastUser(inContext: context!) else {
            return 0
        }
        return Int(lastUser.user_id)
    }
}
