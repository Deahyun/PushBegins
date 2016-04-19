//
//  AppDelegate.m
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 13..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import "AppDelegate.h"
#import "MiniPushObject.h"
#import "ViewController.h"
#import "RegisterVC.h"
#import "PushMsgInfo.h"

@interface AppDelegate ()

@end

@implementation AppDelegate

+ (AppDelegate *)instance {
	return (AppDelegate *)[[UIApplication sharedApplication] delegate];
}


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
	// Override point for customization after application launch.
	
	//
	self.gVars = [[AppVars alloc] init];
	
	// 이 코드를 추가합니다.
	[self registerPush];
	
	
	// For debug
	BOOL bDebug = YES;
	if ( bDebug ) {
		//
	}

	return YES;
}

- (void)applicationWillResignActive:(UIApplication *)application {
	// Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
	// Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
	// Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
	// If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application {
	// Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application {
	// Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
	
	// 실질적인 앱의 시작 부분
	
	// bagde count 를 초기화 한다.
	[[UIApplication sharedApplication] setApplicationIconBadgeNumber: 0];
	[self userStart];
}

- (void)applicationWillTerminate:(UIApplication *)application {
	// Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

// (시작) 이 부분의 코드를 복사합니다.
#pragma mark -
#pragma mark APNS Implement

- (void)application:(UIApplication *)application
didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
	
	// APNS 수신을 위해서 서버에 등록 -> device_token 을 저장한 후 사용자 등록 화면에서 실제로 등록한다.
	self.deviceToken = deviceToken;
	//[[MiniPushObject getInstance] registerAPNS:deviceToken withDeviceId:@"01012342345"];
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
	// APNS 등록 오류 발생시 실행되는 부분
	NSLog(@"Fail to register: %@", [error localizedDescription]);
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo
fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
	//
	if ( application.applicationState == UIApplicationStateInactive ) {
		
		NSLog(@"By Notification Sweep");
		
		// notification center 내용 지우는 코드 추가
		//
		[[UIApplication sharedApplication] setApplicationIconBadgeNumber: 0];
		//[[UIApplication sharedApplication] cancelAllLocalNotifications];
		//
		completionHandler(UIBackgroundFetchResultNewData);
		
	} else if ( application.applicationState == UIApplicationStateBackground ) {
		
		//NSLog(@"Background");
		
		// Refresh the local model
		NSDictionary *aps = [userInfo valueForKey:@"aps"];
		NSString *alert = [aps valueForKey:@"alert"];
		//NSString *strBadge = [aps valueForKey:@"badge"];
		int nBadge = [[aps valueForKey:@"badge"] intValue];
		NSString *sound = [aps valueForKey:@"sound"];
		int nContentAvailable = [[aps valueForKey:@"content-available"] intValue];
		// user_data -> m_id
		NSString *user_data = [userInfo valueForKey:@"user_data"];
		//
		NSLog(@"Background: alert[%@], badge[%i], sound[%@], %i, msg_id[%@]", alert, nBadge, sound, nContentAvailable, user_data);
		
		// APNS 의 특성상 긴 메시지를 push 로 받을수 없으므로, 서버에서 m_id 를 사용해서 받아온다.
		NSString *strUserData = [[MiniPushObject getInstance] verifyMessage:[user_data longLongValue]];
		NSLog(@"user_data: %@", strUserData);
		
		// save to database
		if ( strUserData == nil || [strUserData length] == 0 ) {
			NSLog(@"background::receive push message failure");
			return;
		}
		//
		PushMsgInfo *info = [[PushMsgInfo alloc] init];
		info.m_id = [user_data longLongValue];
		info.alert = alert;
		info.user_data = strUserData;
		//
		[self savePushMessage:info];


		//
		completionHandler(UIBackgroundFetchResultNewData);
		
	} else {
		
		//NSLog(@"Active");
		
		// Show an in-app banner
		NSDictionary *aps = [userInfo valueForKey:@"aps"];
		NSString *alert = [aps valueForKey:@"alert"];
		//NSString *strBadge = [aps valueForKey:@"badge"];
		int nBadge = [[aps valueForKey:@"badge"] intValue];
		NSString *sound = [aps valueForKey:@"sound"];
		int nContentAvailable = [[aps valueForKey:@"content-available"] intValue];
		// user_data -> m_id
		NSString *strMId = [userInfo valueForKey:@"user_data"];
		//
		NSLog(@"Active: alert[%@], badge[%i], sound[%@], %i, msg_id[%@]", alert, nBadge, sound, nContentAvailable, strMId);
		
		// APNS 의 특성상 긴 메시지를 push 로 받을수 없으므로, 서버에서 m_id 를 사용해서 받아온다.
		NSString *strUserData = [[MiniPushObject getInstance] verifyMessage:[strMId longLongValue]];
		NSLog(@"user_data: %@", strUserData);
		
		// save to database
		if ( strUserData == nil || [strUserData length] == 0 ) {
			NSLog(@"background::receive push message failure");
			return;
		}
		//
		PushMsgInfo *info = [[PushMsgInfo alloc] init];
		info.m_id = [strMId longLongValue];
		info.alert = alert;
		info.user_data = strUserData;
		//
		[self savePushMessage:info];

		//
		completionHandler(UIBackgroundFetchResultNewData);
		
	}
}


- (void)registerPush {
#if TARGET_IPHONE_SIMULATOR
	NSLog(@"registerPush");
	return;
#endif
	
	////
	if ([[[UIDevice currentDevice] systemVersion] floatValue] >= 8.0)
	{
		[[UIApplication sharedApplication] registerUserNotificationSettings:
		 [UIUserNotificationSettings settingsForTypes:
		  ( UIRemoteNotificationTypeNewsstandContentAvailability |
		   UIRemoteNotificationTypeBadge |
		   UIRemoteNotificationTypeSound |
		   UIRemoteNotificationTypeAlert )
										   categories:nil]];
		[[UIApplication sharedApplication] registerForRemoteNotifications];
	}
	else
	{
		[[UIApplication sharedApplication] registerForRemoteNotificationTypes:
		 ( UIRemoteNotificationTypeNewsstandContentAvailability |
		  UIRemoteNotificationTypeBadge |
		  UIRemoteNotificationTypeSound |
		  UIRemoteNotificationTypeAlert )];
	}
	//[UIApplication sharedApplication].applicationIconBadgeNumber = 0;
}

// APNS 수신을 원하지 않을 경우 code 상에서 수신되지 않게 처리하는 부분
- (void)unregisterPush {
#if TARGET_IPHONE_SIMULATOR
	NSLog(@"unregisterPush");
	return;
#endif
	
	[[UIApplication sharedApplication] unregisterForRemoteNotifications];
}
// (종료) 이 부분의 코드를 복사합니다.


#pragma mark -
#pragma mart User Implement
- (void)userStart {
	// 등록된 device_id 가 없다면 등록화면을 보여준다.
	NSString *strDeviceId = [[MiniPushObject getInstance] getValue:DEVICE_ID];
	if ( strDeviceId == nil || [strDeviceId length] < 4 ) {
		[self registerProcess:NO];
		return;
	}
	//
}

- (void)registerProcess:(BOOL)animated {
	// Get login screen from storyboard and present it
	UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
	//UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
	RegisterVC *viewController = (RegisterVC *)[storyboard instantiateViewControllerWithIdentifier:@"REGISTER_VC"];
	
	// set navigate url
	//viewController.strUrlParam = START_WEB_URL;
	
	//
	[self.window makeKeyAndVisible];
	[self.window.rootViewController presentViewController:viewController
												 animated:animated
											   completion:nil];
}

- (void)savePushMessage:(PushMsgInfo *)info {
	
	if ( ![[MiniPushObject getInstance] putPushMessage:info] ) {
		NSLog(@"putPushMessage failure");
		return;
	}
	
	// udpate last push message
//	UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
//	ViewController *mainVC = (ViewController *)[storyboard instantiateViewControllerWithIdentifier:@"MAIN_VC"];
//
//	[mainVC updateLastPushMessage];

	// storyboard 방식으로 ViewController 의 pointer 를 얻어서 사용할 경우
	// 실제의 VC 와 매칭되지 않아서 정상적으로 View 가 refresh 되지 않는다.
	// 정상적인 refresh 를 위해서 delegate 를 사용한다.

	// ViewController.m 내부의
	// - (void)onDelegateRefresh:(AppDelegate *)viewController withParam:(PushMsgInfo *)info
	// 함수를 실행시킨다.
	if ( [self.delegate respondsToSelector:@selector(onDelegateRefresh:withParam:)] ) {
		[self.delegate onDelegateRefresh:self withParam:info];
	}
}

////
- (void)alert:(NSString *)strText withTitle:(NSString *)strTitle {
	//NSLog(@"alert");
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:strTitle message:strText delegate:nil cancelButtonTitle:@"확인" otherButtonTitles:nil];
	[alert show];
}

//
- (void)startTimer {
	float fTimerInterval = 3;
	//NSString *strTime = @"1234-11-22 01:02:03";
	self.timer = [NSTimer scheduledTimerWithTimeInterval:fTimerInterval target:self
														 selector:@selector(updateTest) userInfo:nil repeats:NO];
}

- (void)updateTest {
	NSLog(@"updateTest");
	// udpate last push message
	UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
	ViewController *mainVC = (ViewController *)[storyboard instantiateViewControllerWithIdentifier:@"MAIN_VC"];
	mainVC.laReceivedTime.text = @"1234-11-22 01:02:03";
	//mainVC.view.hidden = YES;
}




@end
