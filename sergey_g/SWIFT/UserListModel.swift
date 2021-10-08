//
//  UserListModel.swift
//
//  Created by info@appcodin.com
//

import Foundation


struct UserListModel: Codable {
    let username: String!
    let user_id: Int32!
    let organizations_url: String?
    let received_events_url: String?
    let following_url: String?
    let avatar_url: String?
    let url: String?
    let node_id: String?
    let subscriptions_url: String?
    let repos_url: String?
    let type: String!
    let html_url: String?
    let events_url: String?
    let site_admin: Bool!
    let starred_url: String?
    let gists_url: String?
    let gravatar_id: String?
    let followers_url: String?
    
    enum itemKeys: String, CodingKey {
        case username = "login",
             user_id = "id",
             organizations_url,
             received_events_url,
             following_url,
             avatar_url,
             url,
             node_id,
             subscriptions_url,
             repos_url,
             type,
             html_url,
             events_url,
             site_admin,
             starred_url,
             gists_url,
             gravatar_id,
             followers_url
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: itemKeys.self)
        self.username = try container.decodeIfPresent(String.self, forKey: .username)
        self.user_id = try container.decodeIfPresent(Int32.self, forKey: .user_id)
        self.organizations_url = try container.decodeIfPresent(String.self, forKey: .organizations_url)
        self.received_events_url = try container.decodeIfPresent(String.self, forKey: .received_events_url)
        self.following_url = try container.decodeIfPresent(String.self, forKey: .following_url)
        self.avatar_url = try container.decodeIfPresent(String.self, forKey: .avatar_url) ?? ""
        self.url = try container.decodeIfPresent(String.self, forKey: .url)
        self.node_id = try container.decodeIfPresent(String.self, forKey: .node_id)
        self.subscriptions_url = try container.decodeIfPresent(String.self, forKey: .subscriptions_url)
        self.repos_url = try container.decodeIfPresent(String.self, forKey: .repos_url)
        self.type = try container.decodeIfPresent(String.self, forKey: .type)
        self.html_url = try container.decodeIfPresent(String.self, forKey: .html_url)
        self.events_url = try container.decodeIfPresent(String.self, forKey: .events_url)
        self.site_admin = try container.decodeIfPresent(Bool.self, forKey: .site_admin)
        self.starred_url = try container.decodeIfPresent(String.self, forKey: .starred_url)
        self.gists_url = try container.decodeIfPresent(String.self, forKey: .gists_url)
        self.gravatar_id = try container.decodeIfPresent(String.self, forKey: .gravatar_id)
        self.followers_url = try container.decodeIfPresent(String.self, forKey: .following_url)
    }
}
