//
//  PushDetailVC.h
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 17..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "PushListVC.h"

@class PushMsgInfo;

@interface PushDetailVC : UIViewController <SetPushDelegate>

@property (weak, nonatomic) IBOutlet UILabel *laReceivedTime;
@property (weak, nonatomic) IBOutlet UILabel *laAlert;
@property (weak, nonatomic) IBOutlet UITextView *tvMessage;

@property (strong, nonatomic) PushMsgInfo *m_info;

- (IBAction)onDeleteMessage:(id)sender;

- (void)setPushInfo:(PushMsgInfo *)info;

@end
