//
//  RPChatBotResponse.swift
//
//
//  Created by AppCodin <info@appcodin.com> on 10/26/17.
//  Copyright © 2017 AppCodin. All rights reserved.
//

import UIKit
import SwiftyJSON

enum RPChatBotRequestType:String {
    case freeText = "free_text"
    case quickReply = "quick_reply"
    case imageString = "image_string"
    case dataMatrix = "data_matrix"
}

enum RPChatBotResponseType:Int {
    case waitingForMessage = -2
    case myMessage = -1
    case freeText = 0
    case actions = 1
    case attachment = 2
    case error = 3
}

enum RPChatBotButtonElementType:String {
    case payload = "payload"
    case title = "title"
    case contentType = "content_type"
}

enum RPChatBotAttachentElementType:String {
    case subtitle = "subtitle"
    case title = "title"
    case url = "url"
    case buttons = "buttons"
}

enum RPChatBotButtonActionType:String {
    
    case quickReplyAction = "quick_replies"
    case imageStringAction = "image_string"
    case dataMatrixAction = "data_matrix"
    
    static func getActionType(payload:String) -> RPChatBotButtonActionType {
        if payload == "UPLOAD_FILE" ||  payload == "UPLOAD FILE" {
            return RPChatBotButtonActionType.imageStringAction
        } else if payload == "UPLOAD_MICR" ||  payload == "UPLOAD MICR" {
            return RPChatBotButtonActionType.dataMatrixAction
        } else {
            return RPChatBotButtonActionType.quickReplyAction
        }
    }
}

class RPChatBotButton: NSObject {
    var type:RPChatBotButtonActionType = .quickReplyAction
    var title:String = ""
    var payload:String = ""
    var contectType:String = ""
    
    func setupWithJson(_ josn:JSON) {
        title = josn[RPChatBotButtonElementType.title.rawValue].string ?? ""
        payload = josn[RPChatBotButtonElementType.payload.rawValue].string ?? ""
        contectType = josn[RPChatBotButtonElementType.contentType.rawValue].string ?? ""
        type = RPChatBotButtonActionType.getActionType(payload: payload)
    }
}

class RPChatBotAttachmentElement: NSObject {
    var title:NSAttributedString?
    var subtitle:NSAttributedString?
    var imageUrl:String?
    var buttons:[RPChatBotButton]?
    
    func setupWithJson(_ josn:JSON) {
        let titleString = josn[RPChatBotAttachentElementType.title.rawValue].string ?? ""
        title = titleString.html2AttributedString()
        let subtitleString = josn[RPChatBotAttachentElementType.subtitle.rawValue].string ?? ""
        subtitle = subtitleString.html2ResizableAttributedString()
        
        imageUrl = josn[RPChatBotAttachentElementType.url.rawValue].string ?? ""
        let buttonsData = josn[RPChatBotAttachentElementType.buttons.rawValue].array ?? []
        for buttonData:JSON in buttonsData {
            if buttons == nil {
                buttons = []
            }
            let button:RPChatBotButton = RPChatBotButton()
            button.setupWithJson(buttonData)
            buttons?.append(button)
        }
    }
}


class RPChatBotResponse: NSObject {
    let textName = "text"
    let errorName = "error"
    let attachName = "attachment"
    
    //Common data
    var type:RPChatBotResponseType = .error
    var text:NSAttributedString?
    
    //With actions
    var buttons:[RPChatBotButton]?

    //Attachments
    var attachments:[RPChatBotAttachmentElement]?
    
    func setupMyMessage(_ message:String) -> RPChatBotResponse! {
        type = .myMessage
        text = message.html2AttributedString()
        
        return self
    }
    
    func setupWithData(_ json:JSON) {
        if let error = json[errorName].dictionary {
            let textString = error["retry_count"]?.string ?? ""
            text = textString.html2AttributedString()
            return
        }
            
        if let attach = json[attachName].dictionary {
            type = .attachment
            setupAttachment(attach)

            return
        }
        
        let textString = json[textName].string ?? ""
        text = textString.html2AttributedString()
        if let qr = json[RPChatBotButtonActionType.quickReplyAction.rawValue].array {
            type = .actions
            setupActions(qr)
        } else {
            type = .freeText
        }
    }
    
    func setupAttachment(_ json: [String:JSON]) {
        let attachmentsData = json["payload"]?.dictionary?["elements"]?.array ?? []
        for attachData:JSON in attachmentsData {
            if attachments == nil {
                attachments = []
            }
            let attachment = RPChatBotAttachmentElement()
            attachment.setupWithJson(attachData)
            attachments?.append(attachment)
        }
    }
    
    func setupActions(_ data:[JSON]) {
        for json in data {
            if buttons == nil {
                buttons = []
            }
            let button:RPChatBotButton = RPChatBotButton()
            button.setupWithJson(json)
            buttons?.append(button)
        }
    }
}
