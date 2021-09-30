//
//  RPBarCodeScannerViewController.swift
//
//
//  Created by AppCodin <info@appcodin.com> on 10/25/17.
//  Copyright © 2017 AppCodin. All rights reserved.
//

import UIKit
import AVFoundation

protocol RPBarCodeScannerDelegate {
    func qrCodeDetected(qrCode:String?)
}

class RPBarCodeScannerViewController: UIViewController {

    @IBOutlet weak var doneButton:UIButton!
    
    @IBAction func clickDoneButton(_ sender: Any) {
        delegate?.qrCodeDetected(qrCode: qrCodeString)
        dismiss(animated: true, completion: nil)
    }
    
    var delegate:RPBarCodeScannerDelegate?
    
    private var captureSession:AVCaptureSession?
    private var videoPreviewLayer:AVCaptureVideoPreviewLayer?
    private var qrCodeFrameView:UIView?
    private var qrCodeString:String?
    
    // Added to support different barcodes
    let supportedBarCodes = [AVMetadataObject.ObjectType.qr, AVMetadataObject.ObjectType.code39, AVMetadataObject.ObjectType.code93, AVMetadataObject.ObjectType.upce, AVMetadataObject.ObjectType.pdf417, AVMetadataObject.ObjectType.ean13, AVMetadataObject.ObjectType.aztec, AVMetadataObject.ObjectType.dataMatrix]

    override func viewDidLoad() {
        super.viewDidLoad()
        
        doneButton.scaledFont()
        
        //For simulator only
        #if arch(i386) || arch(x86_64)
            qrCodeString = "KKB 00 0001824145 0067 01025 0000000012942626 27115453996 0000000000000000"
            view.bringSubview(toFront: doneButton)
            doneButton.backgroundColor = UIColor.darkBlue()
            return
        #endif
        
        let captureDevice = AVCaptureDevice.default(for: AVMediaType.video)
        
        do {
            let input = try AVCaptureDeviceInput(device: captureDevice!)
            
            captureSession = AVCaptureSession()
            captureSession?.addInput(input)
            
            let captureMetadataOutput = AVCaptureMetadataOutput()
            captureSession?.addOutput(captureMetadataOutput)
            
            captureMetadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
            captureMetadataOutput.metadataObjectTypes = supportedBarCodes
            
            videoPreviewLayer = AVCaptureVideoPreviewLayer(session: captureSession!)
            videoPreviewLayer?.videoGravity = AVLayerVideoGravity.resizeAspectFill
            videoPreviewLayer?.frame = view.layer.bounds
            view.layer.addSublayer(videoPreviewLayer!)
            
            captureSession?.startRunning()
            
            view.bringSubview(toFront: doneButton)
            doneButton.backgroundColor = UIColor.darkBlue()
            qrCodeFrameView = UIView()
            
            if let qrCodeFrameView = qrCodeFrameView {
                qrCodeFrameView.layer.borderColor = UIColor.darkBlue().cgColor
                qrCodeFrameView.layer.borderWidth = 4
                view.addSubview(qrCodeFrameView)
                view.bringSubview(toFront: qrCodeFrameView)
            }
            
        } catch {
            print(error)
            delegate?.qrCodeDetected(qrCode: nil)
            return
        }
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        if (captureSession?.isRunning == false) {
            captureSession?.startRunning()
        }
    }
    
    override func viewWillLayoutSubviews() {
        super.viewWillLayoutSubviews()
        
        doneButton.setRoundCornerRadius()
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        if (captureSession?.isRunning == true) {
            captureSession?.stopRunning()
        }
    }
}

extension RPBarCodeScannerViewController: AVCaptureMetadataOutputObjectsDelegate {
    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        if metadataObjects.count == 0 {
            qrCodeFrameView?.frame = CGRect.zero
            return
        }
        
        let metadataObj = metadataObjects[0] as! AVMetadataMachineReadableCodeObject
        if supportedBarCodes.contains(metadataObj.type) {
            let barCodeObject = videoPreviewLayer?.transformedMetadataObject(for: metadataObj)
            qrCodeFrameView?.frame = barCodeObject!.bounds
            qrCodeString = metadataObj.stringValue
        }
    }
}
