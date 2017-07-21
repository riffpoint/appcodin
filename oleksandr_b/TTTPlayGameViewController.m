//
//  TTTPlayGameViewController.m
//  Project
//
//  Created by  AppCodin <info@appcodin.com> on 5/22/17.
//  Copyright © 2017 com.appcodin. All rights reserved.
//

#import "TTTPlayGameViewController.h"

#import "TTTScoreViewProtocol.h"
#import "TTTVerticalScoreView.h"
#import "TTTHorizontalScoreView.h"
#import "TTTPlaygroundView.h"

#import "TTTGameController.h"

#import "TTTResultModel.h"

#import "UIAlertController+TTTGame.h"

@interface TTTPlayGameViewController ()<TTTPlaygroundViewDelegate>

@property(nonatomic, strong) TTTGameController *gameController;

@property(nonatomic, strong) IBOutlet TTTVerticalScoreView *playerVerticalScoreView;
@property(nonatomic, strong) IBOutlet TTTVerticalScoreView *aiVerticalScoreView;
@property(nonatomic, strong) IBOutlet TTTHorizontalScoreView *horisontalScoreView;

@property(nonatomic, weak) IBOutlet TTTPlaygroundView *playgroundView;

@end

@implementation TTTPlayGameViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.gameController = [[TTTGameController alloc] initWithViewController:self];
    self.delegate = self.gameController;
    self.playgroundView.delegate = self;
    
    [self orientationSettings];
    
    TTTResultModel *result = [[TTTResultModel alloc] init];
    [result resetResults];
    [self setupScoreViewResult:result];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    
    [self.delegate playgroundDidAppear];
}

#pragma mark - Orientation
- (void)orientationSettings{
    [self setScoreViewsHidden:UIDeviceOrientationIsLandscape([[UIDevice currentDevice] orientation])];
    
    [[UIDevice currentDevice]beginGeneratingDeviceOrientationNotifications];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(orientChanged:)
                                                 name:UIDeviceOrientationDidChangeNotification object:nil];
}

- (void)orientChanged:(NSNotification *)notification {
    [self setScoreViewsHidden:UIDeviceOrientationIsLandscape([[UIDevice currentDevice] orientation])];
}

#pragma mark - Score Views
- (void)setScoreViewsHidden:(BOOL)isHidden {
    if ([UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPhone) {
        self.playerVerticalScoreView.hidden = YES;
        self.aiVerticalScoreView.hidden = YES;
        self.horisontalScoreView.hidden = NO;
    }
    else if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPad) {
        self.playerVerticalScoreView.hidden = !isHidden;
        self.aiVerticalScoreView.hidden = !isHidden;
        self.horisontalScoreView.hidden = isHidden;
    }
}

- (void)setUserLevel:(TTTUserLevel)level {
    for (id<TTTScoreViewProtocol> element in [self arrayOfScoresView]) {
        [element setupUserLevel:level];
    }
}

- (void)setupScoreViewResult:(TTTResultModel *)result {
    for (id<TTTScoreViewProtocol> element in [self arrayOfScoresView]) {
        if (element == self.aiVerticalScoreView) {
            
            TTTResultModel *aiResult = [[TTTResultModel alloc] init];
            aiResult.gamesScore = result.gamesScore;
            aiResult.winsScore = result.losesScore;
            aiResult.tiesScore = result.tiesScore;
            aiResult.losesScore = result.winsScore;
            
            [element setupResult:aiResult];
        }
        else {
            [element setupResult:result];
        }
    }
}

- (NSArray<id <TTTScoreViewProtocol>> *)arrayOfScoresView {
    if ([UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPhone) {
        if (self.horisontalScoreView) {
            return @[self.horisontalScoreView];
        }
    }
    
    if ([UIDevice currentDevice].userInterfaceIdiom == UIUserInterfaceIdiomPad) {
        if (self.horisontalScoreView && self.playerVerticalScoreView && self.aiVerticalScoreView) {
            return @[self.horisontalScoreView, self.playerVerticalScoreView, self.aiVerticalScoreView];
        }
    }
    
    return @[];
}

#pragma mark - Start Game
- (void)startNewGameWithUserLevel:(TTTUserLevel)level {
    [self setUserLevel:level];
}

- (void)startOldGameWithResult:(TTTResultModel *)result {
    self.navigationItem.hidesBackButton = NO;
    [self.playgroundView setEmptyPlayground];
    
    [self.view setUserInteractionEnabled:YES];
}

- (void)startGame:(TTTResultModel *)result userLevel:(TTTUserLevel)level {
    [self setupScoreViewResult:result];
    [self setUserLevel:level];
}

#pragma mark - Moves
- (void)aiMoveAtRow:(NSInteger)row column:(NSInteger)column type:(TTTXOViewType)type {
    [self.view setUserInteractionEnabled:YES];
    [self.playgroundView selectViewWithRow:row
                                    column:column
                                      type:type];
}

- (void)selectViewWithNumber:(NSInteger)rowNumber type:(TTTXOViewType)type
{
    [self.playgroundView selectViewWithNumber:rowNumber type:type];
}

#pragma mark - TTTPlaygroundViewDelegate : User Move
- (void)cellTapped:(TTTXOViewCell *)cell indexPath:(NSIndexPath *)indexPath {
    [self.view setUserInteractionEnabled:NO];
    
    [self playClickSound];
    
    [self.delegate playerTappedIndexPath:indexPath];
}

#pragma mark - End
- (void)postMoveResult:(NSInteger)moveResult row:(NSInteger)row column:(NSInteger)column {
    switch (moveResult) {
        case TTTTurnResultTypeVerticalWin:
            [self.playgroundView showVerticalLineAtColumn:column];
            break;
        case TTTTurnResultTypeHorizontalWin:
            [self.playgroundView showHorizontalLineAtRow:row];
            break;
        case TTTTurnResultTypeLeftRightAslantWin:
            [self.playgroundView showLeftRightAslantLine];
            break;
        case TTTTurnResultTypeRightLeftAslantWin:
            [self.playgroundView showRightLeftAslantLine];
            break;
        default:
            break;
    }
}

- (void)showAlertType:(TTTAletResultType)alertType
               result:(TTTResultModel *)result
              isAgain:(BOOL)isAgain {
    [self setupScoreViewResult:result];
    
    if (isAgain) {
        UIAlertController *alert = [UIAlertController showAlertPlayAgainWithTitle:[self messageForAlert:alertType]
                                                                        okHandler:^(UIAlertAction *action) {
                                                                            [self alertsHandler];
                                                                            [self.delegate startNewGameSelected];
                                                                        }
                                                                    cancelHandler:^(UIAlertAction *action) {
                                                                        [self.navigationController popToRootViewControllerAnimated:YES];
                                                                        [self playClickSound];
                                                                    }];
        
        [self presentViewController:alert animated:YES completion:nil];
    }
    else {
        UIAlertController *alert = [UIAlertController showResultAlertWithTitle:[self messageForAlert:alertType]
                                                                     okHandler:^(UIAlertAction * _Nullable action) {
                                                                         [self alertsHandler];
                                                                         [self.delegate startNextGameSelected];
                                                                     }];
        
        [self presentViewController:alert animated:YES completion:nil];
    }
}

- (void)alertsHandler {
    [self.playgroundView setEmptyPlayground];
    [self playClickSound];
    [self.view setUserInteractionEnabled:YES];
}

- (NSString *)messageForAlert:(TTTAletResultType)alertType {
    NSString *message;
    switch (alertType) {
        case TTTAletResultTypeWin: {
            message = NSLocalizedString(@"MESSAGE_YOU_WIN", nil);
            [self playAudioType:TTTAudioTypeWin];
        }
            break;
        case TTTAletResultTypeLose: {
            message = NSLocalizedString(@"MESSAGE_YOU_LOSE", nil);
            [self playAudioType:TTTAudioTypeLose];
        }
            break;
            
        default: {
            [self playAudioType:TTTAudioTypeWin];
            message = NSLocalizedString(@"MESSAGE_IT_IS_A_TIE", nil);
        }
            break;
    }
    
    return message;
}

@end
