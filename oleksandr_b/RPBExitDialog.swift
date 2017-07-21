//
//  RPBExitDialog.swift
//  GameProject
//
//  Created by AppCodin <info@appcodin.com> on 6/1/17.
//  Copyright © 2017 AppCodin <info@appcodin.com>. All rights reserved.
//

import SpriteKit
import GameplayKit

protocol RPBExitDialogDelegate {
    func yesButtonTapped()
    func noButtonTapped()
}

class RPBExitDialog: SKSpriteNode {
    // MARK: - Constants
    let darkYesButtonImageName = "btn_yes"
    let lightYesButtonImageName = "btn_yes_hover"
    let darkNoButtonImageName = "btn_no"
    let lightNoButtonImageName = "btn_no_hover"
    
    // MARK: - Properties
    var delegateExitDialog : RPBExitDialogDelegate?

    // MARK: - Init
    class func getExitDialog() -> RPBExitDialog {
        let exit = RPBExitDialog.init(texture: SKTexture(imageNamed: "dialog_bg_are_you_sure"))
        exit.setupExitAlert()
        
        return exit
    }
    
    // MARK: - Setup
    func setupExitAlert() {
        position = CGPoint(x: frame.midX, y: frame.midY)
        zPosition = dialogZPosition
        
        let yesButton = RPBButtonNode(normalTexture: SKTexture(imageNamed: lightYesButtonImageName), selectedTexture: SKTexture(imageNamed: darkYesButtonImageName), disabledTexture: SKTexture(imageNamed: lightYesButtonImageName))
        yesButton.setButtonAction(target: self, triggerEvent: .TouchUpInside, action: #selector(RPBExitDialog.yesTap))
        yesButton.name = okButtonName
        yesButton.position = CGPoint(x: -60 , y: -100)
        yesButton.zPosition = dialogsButtonZPosition
        addChild(yesButton)
        
        let noButton = RPBButtonNode(normalTexture: SKTexture(imageNamed: lightNoButtonImageName), selectedTexture: SKTexture(imageNamed: darkNoButtonImageName), disabledTexture: SKTexture(imageNamed: lightNoButtonImageName))
        noButton.setButtonAction(target: self, triggerEvent: .TouchUpInside, action: #selector(RPBExitDialog.noTap))
        noButton.name = okButtonName
        noButton.position = CGPoint(x: 60 , y: -100)
        noButton.zPosition = dialogsButtonZPosition
        addChild(noButton)
    }
    
    func yesTap() {
        delegateExitDialog?.yesButtonTapped()
    }
    
    func noTap() {
        delegateExitDialog?.noButtonTapped()
    }
}
