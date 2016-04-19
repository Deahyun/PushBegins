//
//  HttpClient.h
//  SmartThink
//
//  Created by Deahyun Kim on 14. 5. 21..
//  Copyright (c) 2014년 EnterTera Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface HttpClient : NSObject <NSURLConnectionDelegate>
{
}

- (NSString *)requestString:(NSString *)urlPath withParam:(NSString *)postParam;
- (NSData *)requestData:(NSString *)urlPath withParam:(NSString *)postParam;

@end
