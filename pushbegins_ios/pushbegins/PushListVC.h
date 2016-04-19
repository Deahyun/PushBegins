//
//  PushListVC.h
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 16..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import <UIKit/UIKit.h>

@class PushMsgInfo;

@protocol SetPushDelegate;

@interface PushListVC : UITableViewController <UITableViewDelegate, UITableViewDataSource> {
	NSMutableArray *contentArray;
}

@property (strong, nonatomic) IBOutlet UITableView *myTable;

@property (nonatomic, assign) id<SetPushDelegate> delegate;

- (void)initContent;

- (void)initTable;
- (void)clearTable;

//// 이벤트는 이 클래스에서 발생하고 연결되는 함수는 다른 곳(ViewController)의 것을 사용한다.
//// delegate related
- (void)setPushMessage:(PushMsgInfo *)info;


@end


@protocol SetPushDelegate <NSObject>
@optional
- (void)setPushInfo:(PushListVC *)viewController withParam:(PushMsgInfo *)info;
@end
