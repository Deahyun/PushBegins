//
//  UserDb.m
//  PushBegins
//
//  Created by Deahyun Kim on 2015. 3. 23..
//  Copyright (c) 2015년 EnterTera Inc. All rights reserved.
//

#import "UserDb.h"
#import "TeraDatabase.h"
#import "PushMsgInfo.h"

#define USER_DB_FILE_NAME	@"pushbegins.sqlite"
#define CONFIG_TABLE_NAME	@"tb_config"
#define MSG_CNT_TABLE_NAME	@"tb_message_cnt"
#define PUSH_MSG_TABLE_NAME	@"tb_push_message"


@implementation UserDb

- (id)init
{
	self = [super init];
	if ( self != nil ) {
		// user code here...
		db = [[TeraDatabase alloc] initWithFileName:USER_DB_FILE_NAME];
		[self createTables];
	}
	return self;
}

#pragma mark -
#pragma mark Database Implement
- (void)createTables
{
	NSString *strSql = nil;
	bool bExec;
	
	//
	// CONFIG_TABLE_NAME
	strSql = [NSString stringWithFormat:@"\
			  CREATE TABLE %@ \
			  ( mkey TEXT NOT NULL PRIMARY KEY, \
			  mvalue TEXT NOT NULL );",
			  CONFIG_TABLE_NAME];
	
	bExec = [[db tableNames] containsObject:CONFIG_TABLE_NAME];
	if ( !bExec ) {
		NSLog(@"createTable: %@", CONFIG_TABLE_NAME);
		[db beginTransaction];
		[db executeSql:strSql];
		[db commit];
	}

	// MSG_CNT_TABLE_NAME
	strSql = [NSString stringWithFormat:@"\
			  CREATE TABLE %@ \
			  ( m_id INTEGER NOT NULL PRIMARY KEY );",
			  MSG_CNT_TABLE_NAME];
	
	bExec = [[db tableNames] containsObject:MSG_CNT_TABLE_NAME];
	if ( !bExec ) {
		NSLog(@"createTable: %@", MSG_CNT_TABLE_NAME);
		[db beginTransaction];
		[db executeSql:strSql];
		[db commit];
	}
	
	// PUSH_MSG_TABLE_NAME
	strSql = [NSString stringWithFormat:@"\
			  CREATE TABLE %@ \
			  ( m_id INTEGER NOT NULL PRIMARY KEY, \
			  alert TEXT NOT NULL, \
			  user_data TEXT, \
			  msg_time DATETIME DEFAULT CURRENT_TIMESTAMP );",
			  PUSH_MSG_TABLE_NAME];
	
	bExec = [[db tableNames] containsObject:PUSH_MSG_TABLE_NAME];
	if ( !bExec ) {
		NSLog(@"createTable: %@", PUSH_MSG_TABLE_NAME);
		[db beginTransaction];
		
		//
		[db executeSql:strSql];
		//[db commit];
		//
		strSql = [NSString stringWithFormat:@"\
				  CREATE TRIGGER UPDATE_%@ BEFORE UPDATE ON %@ \
				  BEGIN \
				       UPDATE %@ SET msg_time = datetime('now', 'localtime') \
				       WHERE rowid = new.rowid; \
				  END ",
				  PUSH_MSG_TABLE_NAME, PUSH_MSG_TABLE_NAME, PUSH_MSG_TABLE_NAME];
		//
		[db executeSql:strSql];
		
		//
		strSql = [NSString stringWithFormat:@"\
				  CREATE TRIGGER INSERT_%@ AFTER INSERT ON %@ \
				  BEGIN \
				  UPDATE %@ SET msg_time = datetime('now', 'localtime') \
				  WHERE rowid = new.rowid; \
				  END ",
				  PUSH_MSG_TABLE_NAME, PUSH_MSG_TABLE_NAME, PUSH_MSG_TABLE_NAME];
		//
		[db executeSql:strSql];
		
		//
		[db commit];
	}
}

- (void)dropTables
{
	NSString *strSql = nil;
	bool bExec;
	
	//
	strSql = [NSString stringWithFormat:@"DROP TABLE %@;", CONFIG_TABLE_NAME];
	bExec = [[db tableNames] containsObject:CONFIG_TABLE_NAME];
	if ( bExec ) {
		[db beginTransaction];
		[db executeSql:strSql];
		[db commit];
	}
	
	//
	strSql = [NSString stringWithFormat:@"DROP TABLE %@;", MSG_CNT_TABLE_NAME];
	bExec = [[db tableNames] containsObject:MSG_CNT_TABLE_NAME];
	if ( bExec ) {
		[db beginTransaction];
		[db executeSql:strSql];
		[db commit];
	}
	
	//
	strSql = [NSString stringWithFormat:@"DROP TABLE %@;", PUSH_MSG_TABLE_NAME];
	bExec = [[db tableNames] containsObject:PUSH_MSG_TABLE_NAME];
	if ( bExec ) {
		[db beginTransaction];
		[db executeSql:strSql];
		[db commit];
	}

}

#pragma mark -
#pragma mark Config Table Implement
- (BOOL)setKeyValue:(NSString *)strKey withValue:(NSString *)strValue
{
	if ( [self hasKey:strKey] ) {
		NSString *strDbValue = [self getValue:strKey];
		if ( [strValue isEqualToString:strDbValue] ) {
			// Already has same value
			return YES;
		}
		
		[self deleteKey:strKey];
	}
	
	NSString *strSql = nil;
	strSql = [NSString stringWithFormat:@"\
			  INSERT INTO %@  ( mkey, mvalue ) \
			  VALUES ( '%@', '%@' );",
			  CONFIG_TABLE_NAME, strKey, strValue];
	
	//NSLog(@"setKeyValue: %@", strSql);
	
	[db beginTransaction];
	[db executeSql:strSql];
	[db commit];
	
	return YES;
}

- (BOOL)hasKey:(NSString *)strKey
{
	NSString *strSql = [NSString stringWithFormat:@"SELECT COUNT(*) FROM %@ WHERE mkey = '%@' ",
						CONFIG_TABLE_NAME, strKey];
	NSArray *arrRes = [db executeSql:strSql];
	
	NSString *strDbKey = [NSString stringWithFormat:@"COUNT(*)"];
	NSString *strData = [NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:0] objectForKey:strDbKey]];
	
	int nCount = [strData intValue];
	if ( nCount == 0 ) {
		return NO;
	} else {
		return YES;
	}
}

- (NSString *)getValue:(NSString *)strKey
{
	if ( ![self hasKey:strKey] ) {
		return nil;
	}
	
	NSString *strSql = [NSString stringWithFormat:@"SELECT mvalue FROM %@ WHERE mkey = '%@';",
						CONFIG_TABLE_NAME, strKey];
	NSArray *arrRes = [db executeSql:strSql];
	
	NSString *strDbKey = [NSString stringWithFormat:@"mvalue"];
	NSString *strData = [NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:0] objectForKey:strDbKey]];
	
	return strData;
}

- (void)deleteKey:(NSString *)strKey
{
	if ( ![self hasKey:strKey] ) {
		return;
	}
	
	NSString *strSql = [NSString stringWithFormat:@"DELETE FROM %@ WHERE mkey = '%@' ",
						CONFIG_TABLE_NAME, strKey];
	
	//NSLog(@"deleteKey: %@", strSql);
	
	[db beginTransaction];
	[db executeSql:strSql];
	[db commit];
}

- (void)deleteKeyValue:(NSString *)strKey withValue:(NSString *)strValue
{
	if ( ![self hasKey:strKey] ) {
		return;
	}
	
	NSString *strSql = [NSString stringWithFormat:@"DELETE FROM %@ WHERE mkey = '%@' AND mvalue = '%@' ",
						CONFIG_TABLE_NAME, strKey, strValue];
	
	NSLog(@"deleteKeyValue: %@", strSql);
	
	[db beginTransaction];
	[db executeSql:strSql];
	[db commit];
}

- (BOOL)hasMessageId:(long long)m_id
{
	NSString *strSql = [NSString stringWithFormat:@"SELECT COUNT(m_id) as cnt FROM %@ WHERE m_id = %lld",
						MSG_CNT_TABLE_NAME, m_id];
	NSArray *arrRes = nil;
	@try {
		arrRes = [db executeSql:strSql];
		
	} @catch (NSException * e) {
		NSLog(@"Exception: %@", e);
		return NO;
	}
	
	int nCount = [[NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:0] objectForKey:@"cnt"]] intValue];
	if ( nCount == 0 )
		return NO;
	else
		return YES;

}

- (BOOL)putMessageId:(long long)m_id
{
	if ( [self hasMessageId:m_id] ) {
		return NO;
	}
	
	NSString *strSql = [NSString stringWithFormat:@"INSERT INTO %@ (m_id) VALUES ( %lld )",
						MSG_CNT_TABLE_NAME, m_id];
	
	@try {
		[db beginTransaction];
		[db executeSql:strSql];
		[db commit];
		
	} @catch (NSException *e) {
		NSLog(@"Exception: %@", e);
		[db rollback];
		return NO;
	}
	
	return YES;

}

#pragma mark -
#pragma mark PushMessage Implement
//
- (BOOL)putPushMessage:(long long)m_id withAlert:(NSString *)alert withUserData:(NSString *)user_data
{
	NSString *strSql = [NSString stringWithFormat:@"INSERT INTO %@ ( m_id, alert, user_data ) VALUES ( %lld, '%@', '%@' )",
						PUSH_MSG_TABLE_NAME, m_id, alert, user_data];
//	NSString *strSql = [NSString stringWithFormat:@"INSERT INTO %@ ( m_id, alert, user_data ) VALUES ( ?, ?, ? )",
//						PUSH_MSG_TABLE_NAME];
//	NSMutableArray *arrParams = [[NSMutableArray alloc] init];
//	NSNumber *nId = [NSNumber numberWithLongLong:m_id];
//	[arrParams addObject:nId];
//	[arrParams addObject:alert];
//	[arrParams addObject:user_data];
	

	@try {
		[db beginTransaction];
		[db executeSql:strSql];
		//[db executeSql:strSql withParameters:arrParams];
		[db commit];
		
	} @catch (NSException *e) {
		NSLog(@"Exception: %@", e);
		[db rollback];
		return NO;
	}
	
	return YES;
}

- (BOOL)deletePushMessage:(long long)m_id
{
	NSString *strSql = [NSString stringWithFormat:@" DELETE FROM %@ WHERE m_id = %lld ",
						PUSH_MSG_TABLE_NAME, m_id];
	@try {
		[db beginTransaction];
		[db executeSql:strSql];
		[db commit];
		
	} @catch (NSException *e) {
		NSLog(@"Exception: %@", e);
		[db rollback];
		return NO;
	}
	
	return YES;
}

- (BOOL)deleteAllPushMessage
{
	NSString *strSql = [NSString stringWithFormat:@" DELETE FROM %@ ",
						PUSH_MSG_TABLE_NAME];
	@try {
		[db beginTransaction];
		[db executeSql:strSql];
		[db commit];
		
	} @catch (NSException *e) {
		NSLog(@"Exception: %@", e);
		[db rollback];
		return NO;
	}
	
	return YES;
}
//

- (NSArray *)getAllPushMessageLists
{
	NSMutableArray *arrResult = [[NSMutableArray alloc] init];
	//
	NSString *strSql = [NSString stringWithFormat:@"SELECT m_id, msg_time, alert, user_data FROM %@ ORDER BY m_id DESC ",
						PUSH_MSG_TABLE_NAME];
	NSArray *arrRes = nil;
	@try {
		arrRes = [db executeSql:strSql];
		
	} @catch (NSException * e) {
		NSLog(@"Exception: %@", e);
		return arrResult;
	}
	//
	
	for ( int i = 0; i < [arrRes count]; i++ ) {
		PushMsgInfo *info = [[PushMsgInfo alloc] init];
		//
		info.m_id = [[NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:i] objectForKey:@"m_id"]] longLongValue];
		info.msg_time = [NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:i] objectForKey:@"msg_time"]];
		info.alert = [NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:i] objectForKey:@"alert"]];
		info.user_data = [NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:i] objectForKey:@"user_data"]];
		//
		[arrResult addObject:info];
	}
	//
	return arrResult;
}

- (NSArray *)getPushMessageList:(int)nLimit withOffset:(int)nOffset
{
	NSMutableArray *arrResult = [[NSMutableArray alloc] init];
	NSString *strSql = [NSString stringWithFormat:@"SELECT m_id, msg_time, alert, user_data FROM %@ ORDER BY m_id DESC LIMIT %d OFFSET %d",
						PUSH_MSG_TABLE_NAME, nLimit, nOffset];
	NSArray *arrRes = nil;
	@try {
		arrRes = [db executeSql:strSql];
		
	} @catch (NSException * e) {
		NSLog(@"Exception: %@", e);
		return arrResult;
	}
	//
	
	for ( int i = 0; i < [arrRes count]; i++ ) {
		PushMsgInfo *info = [[PushMsgInfo alloc] init];
		//
		info.m_id = [[NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:i] objectForKey:@"m_id"]] longLongValue];
		info.msg_time = [NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:i] objectForKey:@"msg_time"]];
		info.alert = [NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:i] objectForKey:@"alert"]];
		info.user_data = [NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:i] objectForKey:@"user_data"]];
		//
		[arrResult addObject:info];
	}
	//
	return arrResult;
}

//
- (PushMsgInfo *)getPushMessage:(long long)m_id
{
	PushMsgInfo *info = [[PushMsgInfo alloc] init];
	
	NSString *strSql = [NSString stringWithFormat:@"SELECT m_id, msg_time, alert, user_data FROM %@ WHERE m_id = %lld",
						PUSH_MSG_TABLE_NAME, m_id];
	NSArray *arrRes = nil;
	@try {
		arrRes = [db executeSql:strSql];
		
	} @catch (NSException * e) {
		NSLog(@"Exception: %@", e);
		return info;
	}
	
	info.m_id = [[NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:0] objectForKey:@"m_id"]] longLongValue];
	info.msg_time = [NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:0] objectForKey:@"msg_time"]];
	info.alert = [NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:0] objectForKey:@"alert"]];
	info.user_data = [NSString stringWithFormat:@"%@", [[arrRes objectAtIndex:0] objectForKey:@"user_data"]];
	
	return info;
}


@end
