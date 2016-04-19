//
//  ApnsDeviceInfo.h
//  PushBegins
//
//  Created by Deahyun Kim on 2015. 3. 20..
//  Copyright (c) 2015년 EnterTera Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ApnsDeviceInfo : NSObject

//@property (strong, nonatomic) NSString *udid;
@property (strong, nonatomic) NSString *device_id;
@property (strong, nonatomic) NSString *app_id;
@property (strong, nonatomic) NSString *device_token;

@end
