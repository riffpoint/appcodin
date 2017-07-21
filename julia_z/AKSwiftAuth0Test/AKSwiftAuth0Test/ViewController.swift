//
//  ViewController.swift
//  AKSwiftAuth0Test
//
//  Created by Iuliia Zhelem on 16.06.16.
//  Copyright © 2016 Akvelon. All rights reserved.
//

import UIKit
import Lock
import LockFacebook

class ViewController: UIViewController {
    
    var token: A0Token?

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    @IBAction func clickGetBigPicture(sender: AnyObject) {
        
        if let actualToken = token {
            
            // We need url "https://<Auth0 Domain>/userinfo"
            // and header "Authorization : Bearer <access_token>"
            let userDomain = (NSBundle.mainBundle().infoDictionary!["Auth0Domain"]) as! String
            let urlString = "https://\(userDomain)/userinfo"
            let url = NSURL(string: urlString)
            if let actualUrl = url {
                let request = NSMutableURLRequest(URL: actualUrl)
                request.HTTPMethod = "GET";
                request.allHTTPHeaderFields = ["Authorization" : "Bearer \(actualToken.accessToken!)"]
            
                NSURLSession.sharedSession().dataTaskWithRequest(request, completionHandler: {(data : NSData?, response : NSURLResponse?, error : NSError?) in
                
                    // Check if data was received successfully
                    if error == nil && data != nil {
                        do {
                            // Convert NSData to Dictionary where keys are of type String, and values are of any type
                            let json = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! [String:AnyObject]
                            let urlForBigPicture = json["picture_large"] as! String
                            self.showMessage("Big picture \(urlForBigPicture)");
                        } catch {
                            let dataString = String(data: data!, encoding: NSUTF8StringEncoding)
                            print("Oops something went wrong: \(dataString)")
                        }
                    } else {
                        print("Oops something went wrong: \(error)")
                    }
                }).resume()
            } else {
                self.showMessage("Incorrect url");
            }
        } else {
            self.showMessage("Please login first");
        }
    }
    
    @IBAction func clickFacebookLoginButton(sender: AnyObject) {
        let success = { (profile: A0UserProfile, token: A0Token) in
            print("We did it!. Logged in with Auth0.")
            self.showMessage("We did it!. Logged in with Auth0.");
            self.token = token;
            let urlForBigPicture = profile.extraInfo["picture_large"] as? String
        }
        let failure = { (error: NSError) in
            print("Oops something went wrong: \(error)")
        }
        let lock = A0Lock.sharedLock()
            lock.identityProviderAuthenticator().authenticateWithConnectionName("facebook", parameters: nil, success: success, failure: failure)
        
    }
    
    func showMessage(message: String) {
        dispatch_async(dispatch_get_main_queue()) {
            let alert = UIAlertController(title: "Auth0", message: message, preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
            self.presentViewController(alert, animated: true, completion: nil)
        }
    }
}

