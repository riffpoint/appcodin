//
//  UsersList+CoreDataProperties.swift
//
//  Created by info@appcodin.com
//
//

import Foundation
import CoreData


extension UsersList {

    @nonobjc public class func fetchRequest() -> NSFetchRequest<UsersList> {
        return NSFetchRequest<UsersList>(entityName: "UsersList")
    }

    @NSManaged public var username: String?
    @NSManaged public var user_id: Int32
    @NSManaged public var organizations_url: String?
    @NSManaged public var following_url: String?
    @NSManaged public var avatar_url: String?
    @NSManaged public var url: String?
    @NSManaged public var node_id: String?
    @NSManaged public var subscriptions_url: String?
    @NSManaged public var repos_url: String?
    @NSManaged public var type: String?
    @NSManaged public var html_url: String?
    @NSManaged public var events_url: String?
    @NSManaged public var received_events_url: String?
    @NSManaged public var site_admin: Bool
    @NSManaged public var starred_url: String?
    @NSManaged public var gists_url: String?
    @NSManaged public var gravatar_id: String?
    @NSManaged public var followers_url: String?
    @NSManaged public var isNotes: Bool
    @NSManaged public var reviewed: Bool
    @NSManaged public var notes: String?
    @NSManaged public var relationship: UserProfile?
    
    class func initWithAPIModel(entity: UserListModel, inContext context: NSManagedObjectContext) -> UsersList {
        let user = fetchUserWithID(entity.user_id, inContext: context) ?? UsersList(context: context)
        user.updatedUserFromAPIModel(entity)        
        return user
    }
    
    func updatedUserFromAPIModel(_ entity: UserListModel) {
        self.username = entity.username
        self.user_id = entity.user_id
        self.organizations_url = entity.organizations_url
        self.following_url = entity.followers_url
        self.avatar_url = entity.avatar_url
        self.url = entity.url
        self.node_id = entity.node_id
        self.subscriptions_url = entity.subscriptions_url
        self.repos_url = entity.repos_url
        self.type = entity.type
        self.html_url = entity.html_url
        self.events_url = entity.events_url
        self.received_events_url = entity.received_events_url
        self.site_admin = entity.site_admin
        self.starred_url = entity.starred_url
        self.gists_url = entity.gists_url
        self.gravatar_id = entity.gravatar_id
        self.followers_url = entity.following_url
    }
    
    func addNotesMark(_ notesString: String) {
        CoreDataStack.sharedInstance.performForBackgroundContext { context in
            let user = UsersList.fetchUserWithID(self.user_id, inContext: context)
            if user != nil {
                user?.isNotes = notesString.count > 0 ? true : false
                user?.notes = notesString
            }
            context.saveThrows()
        }
    }
    
    func markAsReviewed() {
        CoreDataStack.sharedInstance.performForBackgroundContext { context in
            let user = UsersList.fetchUserWithID(self.user_id, inContext: context)
            if user != nil {
                user?.reviewed = true
            }
            context.saveThrows()
        }
    }
    
    class func fetchUserWithID(_ user_id: Int32, inContext context: NSManagedObjectContext) -> UsersList? {
        let request: NSFetchRequest<UsersList> = UsersList.fetchRequest()
        request.predicate = NSPredicate(format: "user_id = %d", user_id)
        return (try? context.fetch(request))?.first
    }
    
    class func fetchLastUser(inContext context: NSManagedObjectContext) -> UsersList? {
        let request: NSFetchRequest<UsersList> = UsersList.fetchRequest()
        return (try? context.fetch(request))?.last
    }
}
