//
//  AppVars.m
//  Truvivity
//
//  Created by Deahyun Kim on 2016. 2. 2..
//  Copyright © 2016년 Amway. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "AppVars.h"

@implementation AppVars

- (id)init
{
	self = [super init];
	if ( self != nil ) {
		// user code here...
		[self getiPhoneVersion];
	}
	return self;
}

// iphone3: 320x480(320x480)
// iphone4: 320x480(640x960)
// iphone5: 320x568(640x1136)
// iphone6: 375x667(750x1334)
// iphone6+: 414x736(1080x1920)

- (BOOL)isiPhone4 {
	if ( [[UIScreen mainScreen] bounds].size.height == 480 || [[UIScreen mainScreen] bounds].size.width == 480 ) {
		return YES;
	}
	return NO;
}

- (BOOL)isiPhone5 {
	//CGSize *sz = [[CGSize alloc] init [[UIScreen mainScreen] bounds].size;
	CGSize rt = CGSizeMake([[UIScreen mainScreen] bounds].size.width, [[UIScreen mainScreen] bounds].size.height);
	NSLog(@"%f %f", rt.width, rt.height);
	if ( [[UIScreen mainScreen] bounds].size.height == 568 || [[UIScreen mainScreen] bounds].size.width == 568 ) {
		return YES;
	}
	return NO;
}

- (BOOL)isiPhone6 {
	if ( [[UIScreen mainScreen] bounds].size.height == 667 || [[UIScreen mainScreen] bounds].size.width == 667 ) {
		return YES;
	}
	return NO;
}

- (BOOL)isiPhone6_Plus {
	if ( [[UIScreen mainScreen] bounds].size.height == 736 || [[UIScreen mainScreen] bounds].size.width == 736 ) {
		return YES;
	}
	return NO;
}

- (void)getiPhoneVersion {
	
	// iphone3: 320x480(320x480)
	// iphone4: 320x480(640x960)
	self.vt = iPhone4;
	
	// iPhone4
	if ( [self isiPhone4] ) {
		self.vt = iPhone4;
		self.fMyWidth = 320.0f;
		self.fMyHeight = 480.0f;
	}
	
	
	// iphone5: 320x568(640x1136)
	if ( [self isiPhone5] ) {
		self.vt = iPhone5;
		self.fMyWidth = 320.0f;
		self.fMyHeight = 568.0f;
	}
	
	// iphone6: 375x667(750x1334)
	if ( [self isiPhone6] ) {
		self.vt = iPhone6;
		self.fMyWidth = 375.0f;
		self.fMyHeight = 667.0f;
	}
	
	// iphone6+: 414x736(1080x1920)
	if ( [self isiPhone6_Plus] ) {
		self.vt = iPhone6Plus;
		self.fMyWidth = 414.0f;
		self.fMyHeight = 736.0f;
	}
	
	NSLog(@"VersionType -> %@", [self stringVersionType:self.vt]);
}

- (NSString *)stringVersionType:(VersionType) type {
	NSArray *arr = @[
					 @"iPhone4",
					 @"iPhone5",
					 @"iPhone6",
					 @"iPhone6Plus",
					 ];
	return (NSString *)[arr objectAtIndex:type];
}

@end
