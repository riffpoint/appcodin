//
//  TTTPlayGameViewController.h
//  Project
//
//  Created by  AppCodin <info@appcodin.com> on 5/22/17.
//  Copyright © 2017 com.appcodin. All rights reserved.
//

#import "TTTBaseViewController.h"
#import "TTTUserLevel.h"
#import "XOViewType.h"
#import "TTTTurnResultType.h"

typedef NS_ENUM(NSInteger, TTTAletResultType) {
    TTTAletResultTypeTie,
    TTTAletResultTypeWin,
    TTTAletResultTypeLose
};

@class TTTResultModel;

@protocol TTTPlayGameViewControllerDelegate <NSObject>
@required
- (void)playgroundDidAppear;

- (void)playerTappedIndexPath:(NSIndexPath *)indexPath;

- (void)startNewGameSelected;
- (void)startNextGameSelected;

@end

@interface TTTPlayGameViewController : TTTBaseViewController

@property(nonatomic, weak) id<TTTPlayGameViewControllerDelegate> delegate;

- (void)startGame:(TTTResultModel *)result userLevel:(TTTUserLevel)level;

- (void)aiMoveAtRow:(NSInteger)row
             column:(NSInteger)column
               type:(TTTXOViewType)type;

- (void)selectViewWithNumber:(NSInteger)rowNumber type:(TTTXOViewType)type;

- (void)postMoveResult:(NSInteger)moveResult
                   row:(NSInteger)row
                column:(NSInteger)column;

- (void)showAlertType:(TTTAletResultType)alertType result:(TTTResultModel *)result isAgain:(BOOL)isAgain;

@end
