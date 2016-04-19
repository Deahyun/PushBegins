//
//  AppDelegate.h
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 13..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AppVars.h"

@class PushMsgInfo;

@protocol UserDelegate;

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;
@property (strong, nonatomic) AppVars *gVars;
@property (strong, nonatomic) NSData *deviceToken;

@property (strong, nonatomic) NSTimer *timer;
//@property (nonatomic) int nState;

@property (nonatomic, assign) id<UserDelegate> delegate;

+ (AppDelegate *)instance;

// (시작) 이 부분의 코드를 복사합니다.
- (void)registerPush;
- (void)unregisterPush;
// (종료) 이 부분의 코드를 복사합니다.

- (void)userStart;
- (void)registerProcess:(BOOL)animated;
//- (void)savePushMessage:(PushMsgInfo *)info;

- (void)alert:(NSString *)strText withTitle:(NSString *)strTitle;

//// 이벤트는 이 클래스에서 발생하고 연결되는 함수는 다른 곳(ViewController)의 것을 사용한다.
//// delegate related
- (void)savePushMessage:(PushMsgInfo *)info;

@end

@protocol UserDelegate <NSObject>
@optional
- (void)onDelegateRefresh:(AppDelegate *)viewController withParam:(PushMsgInfo *)info;
@end



