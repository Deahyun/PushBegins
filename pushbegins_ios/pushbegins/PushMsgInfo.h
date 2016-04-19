//
//  PushMsgInfo.h
//  PushBegins
//
//  Created by Deahyun Kim on 2015. 10. 30..
//  Copyright (c) 2015년 EnterTera Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface PushMsgInfo : NSObject

@property (nonatomic) long long m_id;
@property (strong, nonatomic) NSString *alert;
@property (strong, nonatomic) NSString *user_data;
@property (strong, nonatomic) NSString *msg_time;

@end
