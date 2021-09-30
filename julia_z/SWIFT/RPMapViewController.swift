//
//  RPMapViewController.swift
//  ChatBot
//
//  Created by AppCodin <info@appcodin.com> on 11/19/17.
//  Copyright © 2017 AppCodin. All rights reserved.
//

import UIKit
import GoogleMaps

class RPMapViewController: UIViewController {
    
    var latitude:Double = 0.0
    var longitude:Double = 0.0
    var markerTitle:String = ""

    @IBOutlet weak var mapContainerView: GMSMapView!
    
    @IBAction func clickCloseButton(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    override func viewDidLoad() {
        super.viewDidLoad()

        let camera = GMSCameraPosition.camera(withLatitude: latitude, longitude: longitude, zoom: 17.0)
        mapContainerView.camera = camera
        mapContainerView.isMyLocationEnabled = true
        
        // Create a marker in the center of the map.
        let marker = GMSMarker()
        marker.position = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        marker.title = markerTitle
        marker.map = mapContainerView
    }
}
