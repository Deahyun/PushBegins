//
//  PushDetailVC.m
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 17..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import "PushDetailVC.h"
#import "PushMsgInfo.h"
#import "MiniPushObject.h"
#import "AppDelegate.h"

@implementation PushDetailVC

- (void)viewDidLoad {
	[super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
	
	//
	self.title = @"Push Detail";

	//
	self.laReceivedTime.text = self.m_info.msg_time;
	self.laAlert.text = self.m_info.alert;
	self.tvMessage.text = self.m_info.user_data;
	
	//
	//self.tvMessage.editable = NO;
	//self.tvMessage.dataDetectorTypes = UIDataDetectorTypeLink;
}

- (void)viewWillAppear:(BOOL)animated {
}

- (void)didReceiveMemoryWarning {
	[super didReceiveMemoryWarning];
	// Dispose of any resources that can be recreated.
}

//
- (IBAction)onDeleteMessage:(id)sender {
	if ( [[MiniPushObject getInstance] deletePushMessage:self.m_info.m_id] ) {
		[[AppDelegate instance] alert:@"메시지가 삭제되었습니다." withTitle:@"Push 메시지"];
	}
	[self.navigationController popViewControllerAnimated:YES];
}

- (void)setPushInfo:(PushMsgInfo *)info {
	NSLog(@"PushDetailVC::setPushInfo -> %@ / %@ / %@", info.msg_time, info.alert, info.user_data);
	
//	self.laReceivedTime.text = info.msg_time;
//	self.laAlert.text = info.alert;
//	self.tvMessage.text = info.user_data;
	self.m_info = info;
}

#pragma mark -
#pragma mark User Delegate Implement
- (void)setPushInfo:(PushListVC *)viewController withParam:(PushMsgInfo *)info {
	NSLog(@"PushDetailVC::setPushInfo");
	[self setPushInfo:info];
}


@end
