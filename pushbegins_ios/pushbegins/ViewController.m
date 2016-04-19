//
//  ViewController.m
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 13..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import "ViewController.h"
#import "MiniPushObject.h"
#import "PushMsgInfo.h"
#import "ChangeDeviceIdVC.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
	[super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
	
	//
	[[AppDelegate instance] setDelegate:self];
	//
	//self.tvReceived.editable = NO;
	//self.tvReceived.dataDetectorTypes = UIDataDetectorTypeLink;
}

- (void)viewWillAppear:(BOOL)animated {
	NSString *strDeviceId = [[MiniPushObject getInstance] getValue:DEVICE_ID];
	if ( strDeviceId != nil && [strDeviceId length] >= 4 ) {
		self.laDeviceId.text = [NSString stringWithFormat:@"ID: %@", strDeviceId];
	} else {
		self.laDeviceId.text = [NSString stringWithFormat:@"ID: not set"];
	}
	
	[self updateLastPushMessage];
}

- (void)didReceiveMemoryWarning {
	[super didReceiveMemoryWarning];
	// Dispose of any resources that can be recreated.
}

- (IBAction)onChangeDeviceId:(id)sender {
	NSLog(@"onChangeDeviceId");
	if ( NO ) {
		UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
		ChangeDeviceIdVC *vc = (ChangeDeviceIdVC *)[storyboard instantiateViewControllerWithIdentifier:@"CHANGE_DEVICEID_VC"];
		//
		[self.navigationController pushViewController:vc animated:YES];
	}
}

- (IBAction)onPushList:(id)sender {
	NSLog(@"onPushList");
}

- (IBAction)onMessageSend:(id)sender {
	NSLog(@"onMessageSend");
}

//
- (void)updateLastPushMessage {
	//
	NSLog(@"updateLastPushMessage");
	//
//	dispatch_async(dispatch_get_main_queue(), ^{
//	});
	
	PushMsgInfo *info = [[MiniPushObject getInstance] getLastPushMessage];
	NSLog(@"%@ / %@", info.msg_time, info.alert);
	if ( info != nil ) {
		self.laReceivedTime.text = [NSString stringWithFormat:@"%@", info.msg_time];
		self.tvReceived.text = [NSString stringWithFormat:@"[%@]\n\n%@", info.alert, info.user_data];
		//
		[self.laReceivedTime setNeedsDisplay];
		[self.tvReceived setNeedsDisplay];
		
		
	} else {
		self.laReceivedTime.text = [NSString stringWithFormat:@"%@", @"2016-01-01 00:00:00"];
		self.tvReceived.text = [NSString stringWithFormat:@"[쉬운 Push API 서비스 소개] \n\nPushBegins 는 쉽게 push message 를 보낼수 있는 서비스 입니다. \n\n현재 사용중인 앱에 push 를 간단하게 추가할 수 있습니다.\n\n앱 제작에 대한 지식이 전혀 없는 경우에도 기본앱 만들기 서비스를 사용해서 앱을 만들수 있고, 이 앱을 설치한 사용자에게 SMS 처럼 메시지를 보낼수 있습니다."];
	}
	//
}

#pragma mark -
#pragma mark User Delegate Implement
- (void)onDelegateRefresh:(AppDelegate *)viewController withParam:(PushMsgInfo *)info {
	NSLog(@"ViewController::onDelegateRefresh");
	[self updateLastPushMessage];
}

////
//- (void)onRefreshAction:(id)sender {
//	
//	NSLog(@"onRefreshAction");
//	PushMsgInfo *info = (PushMsgInfo *)sender;
//	if ( [self.delegate respondsToSelector:@selector(onDelegateRefresh:withParam:)] ) {
//		[self.delegate onDelegateRefresh:self withParam:info];
//	}
//}


@end
