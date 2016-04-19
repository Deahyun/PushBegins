//
//  ChangeDeviceIdVC.m
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 17..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import "ChangeDeviceIdVC.h"
#import "AppDelegate.h"
#import "MiniPushObject.h"


@implementation ChangeDeviceIdVC

- (void)viewDidLoad {
	[super viewDidLoad];
	// Do any additional setup after loading the view.
	self.title = @"Push 수신 정보";
}

- (void)didReceiveMemoryWarning {
	[super didReceiveMemoryWarning];
	// Dispose of any resources that can be recreated.
}

- (IBAction)onSelect:(id)sender {
	UISegmentedControl *segmentedControl = (UISegmentedControl *)sender;
	NSInteger selectedSegment = segmentedControl.selectedSegmentIndex;
	
	if (selectedSegment == 0) {
		NSLog(@"이메일");
		
	} else if (selectedSegment == 1) {
		NSLog(@"전화번호");
		
	} else if (selectedSegment == 2) {
		NSLog(@"고유 ID");
		
	} else {
		NSLog(@"Unknown");
	}
}

- (IBAction)onSave:(id)sender {
	NSData *deviceToken = [AppDelegate instance].deviceToken;
	NSString *strDeviceId = self.tfDeviceId.text;
	if ( [strDeviceId length] < 4 ) {
		[[AppDelegate instance] alert:@"아이디는 4자리 보다 길어야 합니다." withTitle:@"Push 수신정보"];
		return;
	}
	BOOL bRes = [[MiniPushObject getInstance] registerAPNS:deviceToken withDeviceId:strDeviceId];
	if ( !bRes ) {
		[[AppDelegate instance] alert:@"Push 수정이 실패했습니다." withTitle:@"Push 수신정보"];
		return;
		
	} else {
		[[AppDelegate instance] alert:@"Push 수정을 완료했습니다." withTitle:@"Push 수신정보"];
	}
	
	[self closeVC];
}

- (IBAction)onCancel:(id)sender {
	[self closeVC];
}

// 빈 영역 터치시 키보드 사라지게 한다.
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event {
	[self.tfDeviceId resignFirstResponder];
}

//
#pragma mark User Implement
- (void)closeVC {
	[self.tfDeviceId resignFirstResponder];
	[self.navigationController popViewControllerAnimated:YES];
}


@end
