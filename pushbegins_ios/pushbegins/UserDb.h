//
//  UserDb.h
//  PushBegins
//
//  Created by Deahyun Kim on 2015. 3. 23..
//  Copyright (c) 2015년 EnterTera Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class TeraDatabase;
@class PushMsgInfo;

@interface UserDb : NSObject
{
	TeraDatabase *db;
}


- (BOOL)setKeyValue:(NSString *)strKey withValue:(NSString *)strValue;
- (BOOL)hasKey:(NSString *)strKey;
- (NSString *)getValue:(NSString *)strKey;
- (void)deleteKey:(NSString *)strKey;
- (void)deleteKeyValue:(NSString *)strKey withValue:(NSString *)strValue;

- (void)createTables;
- (void)dropTables;
- (BOOL)hasMessageId:(long long)m_id;
- (BOOL)putMessageId:(long long)m_id;
//
- (BOOL)putPushMessage:(long long)m_id withAlert:(NSString *)alert withUserData:(NSString *)user_data;
- (BOOL)deletePushMessage:(long long)m_id;
- (BOOL)deleteAllPushMessage;
//
- (NSArray *)getAllPushMessageLists;
- (NSArray *)getPushMessageList:(int)nLimit withOffset:(int)nOffset;
- (PushMsgInfo *)getPushMessage:(long long)m_id;


@end
