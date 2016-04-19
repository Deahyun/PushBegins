//
//  SendFeedbackVC.h
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 16..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface SendFeedbackVC : UIViewController
@property (weak, nonatomic) IBOutlet UITextView *tvMessage;

- (IBAction)onSendMessage:(id)sender;
- (IBAction)onCancel:(id)sender;

@end
