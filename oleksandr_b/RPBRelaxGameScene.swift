//
//  RPBRelaxGameScene.swift
//  GameProject
//
//  Created by AppCodin <info@appcodin.com> on 5/5/17.
//  Copyright © 2017 AppCodin <info@appcodin.com>. All rights reserved.
//

import SpriteKit
import GameplayKit

class RPBRelaxGameScene: RPBBaseGameScene {
    // MARK: - Constants
    let offsetScoreImage = CGFloat(60)
    let scoreImageName = "star"
    
    // MARK: - Properties
    let player = RPBPlayer()
    
    // MARK: - Movies
    override func didMove(to view: SKView) {
        setupBackground()
        
        showTopBar()
        showScoreStarAndLabel()
        
        setupPhysicsBody()
    }

    private func showScoreStarAndLabel() {
        let playersScoreImageTexture = SKTexture(imageNamed: scoreImageName)
        playersScoreImage = SKSpriteNode(texture: playersScoreImageTexture)
        playersScoreImage.position = CGPoint(x: offsetScoreImage - topBar.frame.size.width / 2, y: 0)
        topBar.addChild(playersScoreImage)
        
        let vx = offsetScoreImage + playersScoreImage.frame.width - topBar.frame.size.width / 2
        let vy = 0 - playersScoreImage.frame.height / 4
        playersScoreLabel.position = CGPoint(x: vx, y: vy)
        playersScoreLabel.fontSize = CGFloat(playersScoreLabelFontSize)
        playersScoreLabel.fontColor = .black
        playersScoreLabel.text = "0"
        playersScoreLabel.horizontalAlignmentMode = .left
        topBar.addChild(playersScoreLabel)
    }

    // MARK: - Touches
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        
        if gamePaused {
            return
        }

        if let touch = touches.first {
            let position = touch.location(in: self)
            let nodesList = self.nodes(at: position)

            for element in nodesList {
                if element.name == redBallNodeName || element.name == greenBallNodeName {
                    if view != nil {
                        element.removeFromParent()
                        
                        counterDecriment()
                        playersScoreLabel.text = String(player.ballsCounter)
                        
                        return
                    }
                }
            }
            
            counterIncrement()
            playersScoreLabel.text = String(player.ballsCounter)
            
            addBall(isRed: false, point: position)
        }
    }
    
    private func counterDecriment() {
        if player.ballsCounter > 0 {
            player.ballsCounter -= 1
        }
    }
    
    private func counterIncrement() {
        player.ballsCounter += 1
    }
}
