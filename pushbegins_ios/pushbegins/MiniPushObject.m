//
//  MiniPushObject.m
//  MiniPushObject
//
//  Created by Deahyun Kim on 2015. 3. 19..
//  Copyright (c) 2015년 EnterTera Inc. All rights reserved.
//

#import "MiniPushObject.h"
#import "ApnsDeviceInfo.h"
#import "PushMsgInfo.h"
#import "HttpClient.h"
#import "KeychainItemWrapper.h"
#import "UserDb.h"

//#define WEB_URL	@"http://192.168.0.103:8080/pbs_svc/Listener"
#define WEB_URL	@"http://www.pushbegins.com:18080/pbs_svc/Listener"

@implementation MiniPushObject

@synthesize strDeviceToken = _strDeviceToken;
@synthesize nDeviceSeq = _nDeviceSeq;
@synthesize g_db = _g_db;

static MiniPushObject *sharedInstance = nil;

// Singletone Implement
+ (MiniPushObject *)getInstance
{
	//static Singleton *sharedInstance = nil;
	static dispatch_once_t onceToken;
	dispatch_once(&onceToken, ^{
		sharedInstance = [[MiniPushObject alloc] init];
		// Do any other initialisation stuff here
		//g_db = [[UserDb alloc] init];
	});
	return sharedInstance;
}

//
- (id)init
{
	self = [super init];
	if ( self != nil ) {
		// user code here...
		self.g_db = [[UserDb alloc] init];
	}
	return self;
}


#pragma mark -
#pragma mark APNS Implement

- (BOOL)registerAPNS:(NSData *)deviceToken withDeviceId:(NSString *)strDeviceId {
	// app_id 는 bundleIdentifier 과 동일하다.
	// pushbegins 앱에서는 com.entertera.pushbegins
	NSString *bundleIdentifier = [[NSBundle mainBundle] bundleIdentifier];
	NSLog(@"bundleIdentifier -> %@", bundleIdentifier);
	
	//
	if ( NO ) {
		KeychainItemWrapper *wrapper = [[KeychainItemWrapper alloc] initWithIdentifier:@"UUID" accessGroup:nil];
		// EDA5AC92-5E6D-4E0B-A958-218E1655DDF7: iphone4
		// FE389762-9D5F-4F8A-BBDC-9EBFD9322AD1: ipad3
		
		NSString *strUUID = [wrapper objectForKey:(__bridge id)(kSecAttrAccount)];
		if ( strUUID == nil || [strUUID length] == 0 ) {
			//
			CFUUIDRef uuidRef = CFUUIDCreate(NULL);
			CFStringRef uuidStringRef = CFUUIDCreateString(NULL, uuidRef);
			//CFRelease(uuidRef);
			strUUID = [NSString stringWithString:(__bridge NSString *) uuidStringRef];
			//CFRelease(uuidStringRef);
			//NSLog(@"create uuid -> %@", strUUID);
			// save UUID in keychain
			@try {
				[wrapper setObject:strUUID forKey:(__bridge id)(kSecAttrAccount)];
			}
			@catch (NSException *exception) {
				NSLog(@"%@: %@", [exception name], [exception reason]);
			}
			@finally {
				
			}
			//[wrapper setObject:strUUID forKey:(__bridge id)(kSecAttrAccount)];
		}
		//NSLog(@"uuid -> %@", strUUID);
	}
	
	//
	NSString *inDeviceTokenStr = [deviceToken description];
	NSString *tokenString = [inDeviceTokenStr stringByTrimmingCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@"< >"]];
	tokenString = [tokenString stringByReplacingOccurrencesOfString:@" " withString:@""];
	
	// Get DeviceToken
	if ( self.strDeviceToken == nil ) {
		self.strDeviceToken = [[NSString alloc] initWithString:tokenString];
	}
	
	ApnsDeviceInfo *info = [[ApnsDeviceInfo alloc] init];
	//info.udid = strUUID;
	info.device_id = strDeviceId;
	info.device_token = self.strDeviceToken;
	info.app_id = bundleIdentifier;
	
	int nDeviceSeq = [self registerAPNSToServer:info];
	if ( nDeviceSeq > 0 ) {
		self.nDeviceSeq = nDeviceSeq;
		NSString *strDeviceSeq = [NSString stringWithFormat:@"%d", (unsigned int)self.nDeviceSeq];
		//
		[self setKeyValue:DEVICE_SEQ withValue:strDeviceSeq];
		[self setKeyValue:APP_ID withValue:info.app_id];
		[self setKeyValue:DEVICE_ID withValue:strDeviceId];
		
		return YES;
	} else {
		self.nDeviceSeq = 0;
		return NO;
	}
}

//- (BOOL)registerAPNS:(NSData *)deviceToken {
//	//
//	NSString *bundleIdentifier = [[NSBundle mainBundle] bundleIdentifier];
//	NSLog(@"bundleIdentifier -> %@", bundleIdentifier);
//	
//	if ( NO ) {
//		//
//		//NSString *strGroup = [NSString stringWithFormat:@"%@", bundleIdentifier];
//		//KeychainItemWrapper *wrapper = [[KeychainItemWrapper alloc] initWithIdentifier:@"UUID" accessGroup:strGroup];
//		//KeychainItemWrapper *wrapper = [[KeychainItemWrapper alloc] initWithIdentifier:@"UUID" accessGroup:bundleIdentifier];
//		//
//		KeychainItemWrapper *wrapper = [[KeychainItemWrapper alloc] initWithIdentifier:@"UUID" accessGroup:nil];
//		// EDA5AC92-5E6D-4E0B-A958-218E1655DDF7: iphone4
//		// FE389762-9D5F-4F8A-BBDC-9EBFD9322AD1: ipad3
//		
//		NSString *strUUID = [wrapper objectForKey:(__bridge id)(kSecAttrAccount)];
//		if ( strUUID == nil || [strUUID length] == 0 ) {
//			//
//			CFUUIDRef uuidRef = CFUUIDCreate(NULL);
//			CFStringRef uuidStringRef = CFUUIDCreateString(NULL, uuidRef);
//			//CFRelease(uuidRef);
//			strUUID = [NSString stringWithString:(__bridge NSString *) uuidStringRef];
//			//CFRelease(uuidStringRef);
//			//NSLog(@"create uuid -> %@", strUUID);
//			// save UUID in keychain
//			@try {
//				[wrapper setObject:strUUID forKey:(__bridge id)(kSecAttrAccount)];
//			}
//			@catch (NSException *exception) {
//				NSLog(@"%@: %@", [exception name], [exception reason]);
//			}
//			@finally {
//				
//			}
//			//[wrapper setObject:strUUID forKey:(__bridge id)(kSecAttrAccount)];
//		}
//		//NSLog(@"uuid -> %@", strUUID);
//	}
//	
//	//
//	NSString *inDeviceTokenStr = [deviceToken description];
//	NSString *tokenString = [inDeviceTokenStr stringByTrimmingCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@"< >"]];
//	tokenString = [tokenString stringByReplacingOccurrencesOfString:@" " withString:@""];
//	
//	// Get DeviceToken
//	if ( self.strDeviceToken == nil ) {
//		self.strDeviceToken = [[NSString alloc] initWithString:tokenString];
//	}
//	
//	ApnsDeviceInfo *info = [[ApnsDeviceInfo alloc] init];
//	//info.udid = strUUID;
//	// 전화번호를 device_id 로 사용하는 경우
//	info.device_id = @"01012342345";
//	info.device_token = self.strDeviceToken;
//	info.app_id = bundleIdentifier;
//	
//	int nDeviceSeq = [self registerAPNSToServer:info];
//	if ( nDeviceSeq > 0 ) {
//		self.nDeviceSeq = nDeviceSeq;
//		return YES;
//	} else {
//		self.nDeviceSeq = 0;
//		return NO;
//	}
//}

- (int)registerAPNSToServer:(ApnsDeviceInfo *)info {
	int nResult = 0;
	
	HttpClient *client = [[HttpClient alloc] init];
	NSString *strUrl = [NSString stringWithFormat:@"%@", WEB_URL];
	//NSData *dataRes = [client requestData:strUrl withParam:@"{\"io_kind\":\"list_req\"}"];
	
	//
//	//NSDictionary *json = ...; // 인코딩 하려는 사전 타입 객체
//	NSMutableDictionary *diJson = [[NSMutableDictionary alloc] init];
//	[diJson setObject:@"update_apns_device_info_req" forKey:@"io_kind"];
//	[diJson setObject:info.udid forKey:@"udid"];
//	[diJson setObject:info.device_id forKey:@"device_id"];
//	[diJson setObject:info.device_token forKey:@"device_token"];
//	[diJson setObject:info.app_id forKey:@"app_id"];
//	
//	NSError *error = nil;
//	NSData *jsonObj = [NSJSONSerialization dataWithJSONObject:diJson
//													  options:0
//														error:&error];
//	if (error) {
//		NSLog(@"Failed to encode JSON with object: %@", diJson);
//		return nResult;
//	}
//	
//	//
//	NSString *strParam = [NSString stringWithUTF8String:[jsonObj bytes]];
//	if ( strParam == nil || [strParam length] == 0 ) {
//		return nResult;
//	}
	// Dictionary 사용은 ARC 로 인한 오류가 있는듯... 이전에 사용되던 값이 겹쳐져서 나타난다.
	// 직접 json 형식을 만들어서 사용한다.
	NSString *strParam = [NSString stringWithFormat:
						  @"{\"io_kind\":\"update_apns_device_info_req\", \"device_id\":\"%@\", \"device_token\":\"%@\", \"app_id\":\"%@\"}",
						  info.device_id, info.device_token, info.app_id];
	NSLog(@"token -> [%@]", info.device_token);

	
	NSData *dataRes = [client requestData:strUrl withParam:strParam];
	if (dataRes == nil )
		return nResult;
	
	//
	NSError *error = nil;
	NSDictionary *resultDict = [NSJSONSerialization JSONObjectWithData:dataRes options:kNilOptions error:&error];
	NSLog(@"%@", resultDict);
	NSString *strResult = [resultDict objectForKey:@"result"];
	if ( [ strResult isEqualToString:@"yes"] ) {
		NSString *strDeviceSeq = [resultDict objectForKey:@"device_seq"];
		int nDeviceSeq = [strDeviceSeq intValue];
		nResult = nDeviceSeq;
		
	} else {
		NSString *strError = [resultDict objectForKey:@"error_message"];
		[self alert:strError withTitle:@"registerAPNSToServer"];
	}
	
	return nResult;
}

//

- (NSString *)verifyMessage:(long long)m_id {
	if ( m_id <= 0 ) {
		return @"";
	}
	
	HttpClient *client = [[HttpClient alloc] init];
	NSString *strUrl = [NSString stringWithFormat:@"%@", WEB_URL];
	
//	//
//	NSNumber *nMessageId = [[NSNumber alloc] initWithLongLong:m_id];
//	//NSDictionary *json = ...; // 인코딩 하려는 사전 타입 객체
//	NSMutableDictionary *diJson = [[NSMutableDictionary alloc] init];
//	[diJson setObject:@"verify_message_id_req" forKey:@"io_kind"];
//	[diJson setObject:nMessageId forKey:@"m_id"];
//	//NSLog(@"count: %i", [diJson count]);
//	
//	NSError *error = nil;
//	NSData *jsonObj = [NSJSONSerialization dataWithJSONObject:diJson
//													  options:0
//														error:&error];
//	if (error) {
//		NSLog(@"Failed to encode JSON with object: %@", diJson);
//		return nil;
//	}
//	//NSMutableData *jsData = [[NSMutableData alloc] initWithData:jsonObj];
//	//
//	NSString *strParam = [NSString stringWithUTF8String:[jsonObj bytes]];
//	//NSString *strParam = [NSString stringWithUTF8String:[jsData bytes]];
	
	if ( [self.g_db hasMessageId:m_id] ) {
		PushMsgInfo *info = [self.g_db getPushMessage:m_id];
		return info.user_data;
	}
	
	// ARC 로 인한 오류가 있는듯... 이전에 사용되던 값이 겹쳐져서 나타난다.
	NSString *strParam = [NSString stringWithFormat:
						  @"{\"io_kind\":\"verify_message_id_req\", \"m_id\":%lld}", m_id];
	
	//NSLog(@"send -> %@", strParam);
	NSData *dataRes = [client requestData:strUrl withParam:strParam];
	if ( dataRes == nil )
		return @"";
	//
	//[jsData setLength:0];
	
	//
	NSError *error = nil;
	NSDictionary *resultDict = [NSJSONSerialization JSONObjectWithData:dataRes options:kNilOptions error:&error];
	//NSLog(@"verifyMessage: response -> %@", resultDict);
	NSString *strUserData = nil;
	NSString *strResult = [resultDict objectForKey:@"result"];
	if ( [ strResult isEqualToString:@"yes"] ) {
		NSString *user_data = [resultDict objectForKey:@"user_data"];
		if ( user_data == nil || [user_data length] == 0 ) {
			strUserData = @"none";
		} else  {
			strUserData = [resultDict objectForKey:@"user_data"];
		}
		//
		if ( ![self.g_db putMessageId:m_id] ) {
			NSLog(@"record message id failure");
		}
		
	} else {
		strUserData = @"";
	}
	
	return strUserData;
}

- (BOOL)sendFeedbackMessage:(NSString *)strMessage {
	if ( strMessage == nil || [strMessage length] == 0 ) {
		return NO;
	}
		//
	NSString *strDeviceDeq = [self.g_db getValue:DEVICE_SEQ];
	NSString *strAppId = [self.g_db getValue:APP_ID];
	
	if ( strDeviceDeq == nil || [strDeviceDeq length] == 0 ) {
		return NO;
	}
	if ( strAppId == nil || [strAppId length] == 0 ) {
		return NO;
	}
	
	//
	HttpClient *client = [[HttpClient alloc] init];
	NSString *strUrl = [NSString stringWithFormat:@"%@", WEB_URL];
	
	NSString *strParam = [NSString stringWithFormat:
						  @"{\"io_kind\":\"send_customer_message_i_req\", \"device_seq\":%@, \"app_id\":\"%@\", \"message\":\"%@\"}",
						  strDeviceDeq, strAppId, strMessage];
	//NSLog(@"send -> %@", strParam);
	NSData *dataRes = [client requestData:strUrl withParam:strParam];
	if ( dataRes == nil )
		return NO;
	//
	NSError *error = nil;
	NSDictionary *resultDict = [NSJSONSerialization JSONObjectWithData:dataRes options:kNilOptions error:&error];
	NSLog(@"sendFeedbackMessage: response -> %@", resultDict);
	NSString *strResult = [resultDict objectForKey:@"result"];
	if ( [ strResult isEqualToString:@"yes"] ) {
		return YES;
		
	} else {
		return NO;
	}
	//return YES;
}

#pragma mark -
#pragma makr Push Message Implement
- (PushMsgInfo *)getPushMessage:(long long)m_id {
	return [self.g_db getPushMessage:m_id];
}

- (PushMsgInfo *)getLastPushMessage {
	PushMsgInfo *infoRes = [[PushMsgInfo alloc] init];
	
	//
	//return [self.g_db getValue:strKey];
	NSArray *arrResult = [self.g_db getPushMessageList:1 withOffset:0];
	if ( arrResult == nil || [arrResult count] == 0 ) {
		return nil;
	}
	
	infoRes = (PushMsgInfo *)[arrResult objectAtIndex:0];
	return infoRes;
}

- (BOOL)putPushMessage:(long long)m_id withAlert:(NSString *)alert withUserData:(NSString *)user_data {
	
	return [self.g_db putPushMessage:m_id withAlert:alert withUserData:user_data];
}

- (BOOL)putPushMessage:(PushMsgInfo *)info {
	return [self putPushMessage:info.m_id withAlert:info.alert withUserData:info.user_data];
}

//
- (NSArray *)getPushMessageList:(int)nLimit withOffset:(int)nOffset {
	return [self.g_db getPushMessageList:nLimit withOffset:nOffset];
}

- (NSArray *)getAllPushMessageLists {
	return [self.g_db getAllPushMessageLists];
}

- (BOOL)deletePushMessage:(long long)m_id {
	return [self.g_db deletePushMessage:m_id];
}


////
- (void)alert:(NSString *)strText withTitle:(NSString *)strTitle {
	//NSLog(@"alert");
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:strTitle message:strText delegate:nil cancelButtonTitle:@"확인" otherButtonTitles:nil];
	[alert show];
}



//////////
#define BASIC_URL			@"http://www.entertera.com:18080/adidas_golf/list_news.jsp"
- (NSArray *)getMessageList:(NSString *)strParam
{
	HttpClient *client = [[HttpClient alloc] init];
	NSString *strUrl = [NSString stringWithFormat:@"%@", BASIC_URL];
	NSData *dataRes = [client requestData:strUrl withParam:@"{\"io_kind\":\"list_req\"}"];
	
	NSError *error;
	NSDictionary *resultDict = [NSJSONSerialization JSONObjectWithData:dataRes options:kNilOptions error:&error];
//	NSLog(@"%@", resultDict);
//	for (NSString *key in resultDict) {
//		id value = [resultDict objectForKey:key];
//		//NSLog(@"%@ ==> %@", key, value);
//	}
//	NSLog(@"%i", [resultDict count]);
	
	NSArray *resArray = [resultDict objectForKey:@"news_list"];
	for ( int i = 0; i < [resArray count]; i++ ) {
		NSDictionary *subDict = [NSDictionary dictionaryWithDictionary:[resArray objectAtIndex:i]];
		NSLog(@" ---- [%i] ----", i);
		for (NSString *key in subDict) {
			if ( [key isEqualToString:@"content"] ) {
				continue;
			}
			id value = [subDict objectForKey:key];
			NSLog(@"%@ ==> %@", key, value);
		}
	}
	
	NSArray *resultArray = [[NSArray alloc] initWithArray:resArray];
	return resultArray;
	//	return nil;
}

// Config table
- (BOOL)setKeyValue:(NSString *)strKey withValue:(NSString *)strValue {
	return [self.g_db setKeyValue:strKey withValue:strValue];
}

- (NSString *)getValue:(NSString *)strKey {
	return [self.g_db getValue:strKey];
}

- (void)deleteKey:(NSString *)strKey {
	[self.g_db deleteKey:strKey];
}



@end
