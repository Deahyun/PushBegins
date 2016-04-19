//
//  HttpClient.m
//  SmartThink
//
//  Created by Deahyun Kim on 14. 5. 21..
//  Copyright (c) 2014년 EnterTera Inc. All rights reserved.
//

#import "HttpClient.h"

@implementation HttpClient

- (NSString *)requestString:(NSString *)urlPath withParam:(NSString *)postParam
{
	NSData *receivedData = [self requestData:urlPath withParam:postParam];
	NSString *strRes = [[NSString alloc] initWithData:receivedData encoding:NSUTF8StringEncoding];
	return strRes;
}

- (NSData *)requestData:(NSString *)urlPath withParam:(NSString *)postParam
{
	// make post data
	//NSString *strPost = [NSString stringWithFormat:@"value1=%@&value2=%@", @"2345", @"211"];
	//NSData *postData = [strPost dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:YES];
	NSData *postData = [postParam dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:YES];
	NSString *postLength = [NSString stringWithFormat:@"%lu", (unsigned long)[postData length]];
	
	// make request
	//NSURL *url = [[NSURL alloc] initWithString:urlPath];
	//NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
	NSMutableURLRequest *request = [NSMutableURLRequest
									requestWithURL:[NSURL URLWithString:urlPath]
									cachePolicy:NSURLRequestReloadIgnoringCacheData
									timeoutInterval:20.0];
	//
	[request setValue:@"Mozilla/4.0 (compatible;)" forHTTPHeaderField:@"User-Agent"];
	[request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
	//
	[request setHTTPMethod:@"POST"];
	[request setValue:postLength forHTTPHeaderField:@"Content-Length"];
	[request setHTTPBody:postData];
	
	//
	NSURLResponse *res = nil;
	NSError *error = nil;
	
	//
	NSData *receivedData = [NSURLConnection sendSynchronousRequest:request returningResponse:&res error:&error];
	if ( error ) {
		NSLog(@"requestData error: %@", [error localizedDescription]);
	}
	//NSString *strRes = [[NSString alloc] initWithData:receivedData encoding:NSUTF8StringEncoding];
	//NSLog(@"%@", strRes);

	//
	return receivedData;
}

@end
