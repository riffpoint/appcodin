//
//  NetworkManager.swift
//
//  Created by info@appcodin.com
//

import Foundation


struct Wrapper<T: Codable>: Codable {
    let items: [T]
}

enum APIEndpointType {
    case usersList
    case userProfile
}

extension URL {
    init(staticString string: StaticString) {
        guard let url = URL(string: "\(string)") else {
            preconditionFailure("Invalid static URL string: \(string)")
        }
        
        self = url
    }
    
    func appending(_ queryItems: [URLQueryItem]) -> URL? {
        guard var urlComponents = URLComponents(url: self, resolvingAgainstBaseURL: true) else {
            return nil
        }
        urlComponents.queryItems = (urlComponents.queryItems ?? []) + queryItems
        return urlComponents.url
    }
    
    func apendPath(pathString: String) -> URL? {
        return self.appendingPathComponent(pathString)
    }
}

class NetworkManager {
    
    fileprivate class func sendRequest(request: URLRequest,
                                       successCompletion: @escaping (Data) -> Void,
                                       failureCompletion: @escaping (Error) -> Void) -> () {
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            if let error = error {
                failureCompletion(error)
            }
            
            if let data = data {
                successCompletion(data)
            } else {
                failureCompletion(error!)
            }
        }
        
        task.resume()
    }
    
    fileprivate class func apiRequest(apiMethod: APIEndpointType, queryItems: [URLQueryItem]?, path: String) -> URLRequest {
        var url = URL(string: "https://api.github.com/users")
       
        switch apiMethod {
        case .usersList:
            url = url!.appending(queryItems!)
        case .userProfile:
            url = url!.apendPath(pathString: "/\(path)")
        }
        
        
        return URLRequest(url: url!)
    }
    
    class func downloadUsersList(pageNumber: Int,
                                 successComplition: @escaping (Data) -> Void,
                                 failureComplition: @escaping (Error) -> Void) -> () {
        
        let request = apiRequest(apiMethod: .usersList,
                                 queryItems: [URLQueryItem(name: "since", value: "\(pageNumber)")],
                                 path: "")
        
        sendRequest(request: request, successCompletion: successComplition, failureCompletion: failureComplition)
    }
    
    class func downloadUserProfile(username: String,
                                   successComplition: @escaping (Data) -> Void,
                                   failureComplition: @escaping (Error) -> Void) -> () {
        
        let request = apiRequest(apiMethod: .userProfile,
                                 queryItems: nil,
                                 path: username)

        sendRequest(request: request, successCompletion: successComplition, failureCompletion: failureComplition)
    }
}
