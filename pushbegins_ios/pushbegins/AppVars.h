//
//  AppVars.h
//  PushBegins
//
//  Created by Deahyun Kim on 2015. 10. 12..
//  Copyright (c) 2015년 EnterTera Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum _VersionType : NSInteger
{
	iPhone4,
	iPhone5,
	iPhone6,
	iPhone6Plus
	
} VersionType;


@interface AppVars : NSObject

@property (nonatomic) VersionType vt;
//
@property (nonatomic) float fMyWidth;
@property (nonatomic) float fMyHeight;
//
@property (nonatomic) float fTitleCellHeight;
@property (nonatomic) float fContentCellHeight;

- (BOOL)isiPhone4;
- (BOOL)isiPhone5;
- (BOOL)isiPhone6;
- (BOOL)isiPhone6_Plus;

- (void)getiPhoneVersion;

- (NSString *)stringVersionType:(VersionType) type;


@end
