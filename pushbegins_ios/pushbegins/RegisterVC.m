//
//  RegisterVC.m
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 14..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import "RegisterVC.h"
#import "AppDelegate.h"
#import "MiniPushObject.h"

@interface RegisterVC ()

@end

@implementation RegisterVC

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
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

- (IBAction)onSelect:(id)sender {
	//NSLog(@"", self.seIdKind)
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
		[[AppDelegate instance] alert:@"아이디는 4자리 보다 길어야 합니다." withTitle:@"Push 등록"];
		return;
	}
	BOOL bRes = [[MiniPushObject getInstance] registerAPNS:deviceToken withDeviceId:strDeviceId];
	if ( !bRes ) {
		[[AppDelegate instance] alert:@"Push 등록이 실패했습니다." withTitle:@"Push 등록"];
		return;
		
	} else {
		[[AppDelegate instance] alert:@"Push 등록을 완료했습니다." withTitle:@"Push 등록"];
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
	[self dismissViewControllerAnimated:YES completion:^{
		NSLog(@"After close VC...");
	}];
	
	//[self dismissModalViewControllerAnimated:YES];
}



@end
