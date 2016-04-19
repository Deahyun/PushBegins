//
//  ViewController.h
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 13..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AppDelegate.h"

@class PushMsgInfo;


@interface ViewController : UIViewController <UserDelegate>

@property (weak, nonatomic) IBOutlet UILabel *laDeviceId;
@property (weak, nonatomic) IBOutlet UILabel *laReceivedTime;
@property (weak, nonatomic) IBOutlet UITextView *tvReceived;

- (IBAction)onChangeDeviceId:(id)sender;
- (IBAction)onPushList:(id)sender;
- (IBAction)onMessageSend:(id)sender;

- (void)updateLastPushMessage;

@end

