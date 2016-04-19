//
//  SendFeedbackVC.m
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 16..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import "SendFeedbackVC.h"
#import "AppDelegate.h"
#import "MiniPushObject.h"

@interface SendFeedbackVC ()

@end

@implementation SendFeedbackVC

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
	
	self.title = @"메시지 보내기";
}

- (void)viewWillAppear:(BOOL)animated {
	self.tvMessage.text = @"";
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

- (IBAction)onSendMessage:(id)sender {
	NSLog(@"onSendMessage");
	//- (BOOL)sendFeedbackMessage:(NSString *)strMessage {
	NSString *strMessage = self.tvMessage.text;
	BOOL bRes = [[MiniPushObject getInstance] sendFeedbackMessage:strMessage];
	if ( bRes ) {
		[self.tvMessage resignFirstResponder];
		[[AppDelegate instance] alert:@"피드백 메시지가 전송되었습니다." withTitle:@"피드백 메시지"];
		[self.navigationController popViewControllerAnimated:YES];
		
	} else {
		[self.tvMessage resignFirstResponder];
		[[AppDelegate instance] alert:@"피드백 메시지 전송이 실패했습니다." withTitle:@"피드백 메시지"];
	}
}

- (IBAction)onCancel:(id)sender {
	[self.tvMessage resignFirstResponder];
	[self.navigationController popViewControllerAnimated:YES];
}

@end
