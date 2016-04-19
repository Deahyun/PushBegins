//
//  MiniPushObject.h
//  MiniPushObject
//
//  Created by Deahyun Kim on 2015. 3. 19..
//  Copyright (c) 2015년 EnterTera Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

#define DEVICE_SEQ	@"device_seq"
#define APP_ID		@"app_id"
#define DEVICE_ID	@"device_id"


@class UserDb;
@class ApnsDeviceInfo;
@class PushMsgInfo;

@interface MiniPushObject : NSObject


@property (strong, nonatomic) NSString *strDeviceToken;
@property (nonatomic) NSUInteger nDeviceSeq;
@property (strong, nonatomic) UserDb *g_db;

+ (MiniPushObject *)getInstance;


//- (BOOL)registerAPNS:(NSData *)deviceToken;
//- (BOOL)registerAPNS:(NSData *)deviceToken withPhone:(NSString *)strPhone;
- (BOOL)registerAPNS:(NSData *)deviceToken withDeviceId:(NSString *)strDeviceId;
- (NSArray *)getMessageList:(NSString *)strParam;
- (int)registerAPNSToServer:(ApnsDeviceInfo *)info;
- (NSString *)verifyMessage:(long long)m_id;


- (PushMsgInfo *)getPushMessage:(long long)m_id;
- (PushMsgInfo *)getLastPushMessage;
- (BOOL)putPushMessage:(long long)m_id withAlert:(NSString *)alert withUserData:(NSString *)user_data;
- (BOOL)putPushMessage:(PushMsgInfo *)info;
- (NSArray *)getPushMessageList:(int)nLimit withOffset:(int)nOffset;
- (NSArray *)getAllPushMessageLists;
- (BOOL)deletePushMessage:(long long)m_id;

- (BOOL)sendFeedbackMessage:(NSString *)strMessage;



// MiniPushObject database function
- (BOOL)setKeyValue:(NSString *)strKey withValue:(NSString *)strValue;
- (NSString *)getValue:(NSString *)strKey;
- (void)deleteKey:(NSString *)strKey;

//
- (void)alert:(NSString *)strText withTitle:(NSString *)strTitle;

@end