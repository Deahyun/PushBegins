//
//  PushListVC.m
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 16..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import "PushListVC.h"
#import "MiniPushObject.h"
#import "UserDef.h"
#import "PushMsgInfo.h"
#import "PushInfoCell.h"
#import "PushDetailVC.h"

@interface PushListVC ()

@end

@implementation PushListVC

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    //self.navigationItem.rightBarButtonItem = self.editButtonItem;

	//
	[self initTable];
}

- (void)viewWillAppear:(BOOL)animated {
	[self initContent];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

//#pragma mark - Table view data source
//- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
////#warning Incomplete implementation, return the number of sections
//    return 0;
//}
//
//- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
//#warning Incomplete implementation, return the number of rows
//    return 0;
//}

/*
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:<#@"reuseIdentifier"#> forIndexPath:indexPath];
    
    // Configure the cell...
    
    return cell;
}
*/

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath {
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

#pragma mark -
#pragma mark User Implement
- (void)initContent {
	
	if ( NO ) {
		NSArray *arr = [self.navigationController viewControllers];
		NSUInteger i = [arr indexOfObject:self];
		if ( i != 0 ) {
			return;
		}
	}
	if ( contentArray == nil ) {
		contentArray = [[NSMutableArray alloc] init];
	}
	[contentArray removeAllObjects];
	contentArray =  [NSMutableArray arrayWithArray:[[MiniPushObject getInstance] getAllPushMessageLists]];
	[self.myTable reloadData];
	
	//NSLog(@"count: %lu", (unsigned long)[contentArray count]);
//	for ( int i = 0; i < [contentArray count]; i++ ) {
//		PushMsgInfo *info = [contentArray objectAtIndex:i];
//		NSLog(@"%lld / %@ / %@ / %@", info.m_id, info.msg_time, info.alert, info.user_data);
//	}
	
//	PushMsgInfo *info = (PushMsgInfo *)[contentArray objectAtIndex:row];
//	if ( info == nil ) {
//		return nil;
//	}

}

#pragma mark -
#pragma mark Init TableView
- (void)initTable {
	//
	self.myTable.separatorColor = SEPERATOR_COLOR;
	//self.myTable.backgroundColor = SIDE_TABLE_BG_COLOR;
	//
	if ([self.myTable respondsToSelector:@selector(setSeparatorInset:)]) {
		[self.myTable setSeparatorInset:UIEdgeInsetsZero];
	}
	if ([self.myTable respondsToSelector:@selector(setLayoutMargins:)]) {
		[self.myTable setLayoutMargins:UIEdgeInsetsZero];
	}
	
	//self.view.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@"bg_screen.png"]];
}
//
//- (void)clearTable {
//	[self.myTable removeFromSuperview];
//	self.myTable = nil;
//}

#pragma mark -
#pragma mark TableView DataSource
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	
	return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	
	return [contentArray count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	
	if ( tableView != self.myTable ) {
		return nil;
	}

	NSUInteger row = [indexPath row];
	//NSUInteger section = [indexPath section];
	
	//
	PushMsgInfo *info = (PushMsgInfo *)[contentArray objectAtIndex:row];
	if ( info == nil ) {
		return nil;
	}
	
	static NSString *CellIdentifier = @"PushInfoCell";
	PushInfoCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
	if (cell == nil) {
		cell = [[PushInfoCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
	}

	cell.time.text = info.msg_time;
	cell.alert.text = info.alert;
	cell.message.text = info.user_data;

	return cell;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{
	//NSUInteger section = [indexPath section];
	//NSUInteger row = [indexPath row];
	
//	if ( tableView == self.myTable ) {
//		//
//		
//	} else	{
//		//
//	}
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	
	if ( tableView != self.myTable ) {
		return;
	}
	
	//NSUInteger section = [indexPath section];
	NSUInteger row = [indexPath row];
	//NSUInteger section = [indexPath section];
	
	//selectedIndexPath = indexPath;
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	
	NSLog(@"%lu selected", (unsigned long)row);
	[tableView deselectRowAtIndexPath:indexPath animated:YES];
	
	//
	PushMsgInfo *info = [contentArray objectAtIndex:row];
	if ( info == nil ) {
		return;
	}
	
	// with delegate
	//[self setPushMessage:info];
	
	//
	UIStoryboard *storyboard = [UIStoryboard storyboardWithName:@"Main" bundle:[NSBundle mainBundle]];
	PushDetailVC *vc = (PushDetailVC *)[storyboard instantiateViewControllerWithIdentifier:@"PUSH_DETAIL_VC"];
	if ( YES ) {
		[vc setPushInfo:info];
	} else {
		[self setDelegate:vc];
		[self setPushMessage:info];
	}
	//
	[self.navigationController pushViewController:vc animated:YES];
}

- (void)setPushMessage:(PushMsgInfo *)info {
	
	// PushDetailVC.m 내부의
	// - (void)setPushInfo:(PushListVC *)viewController withParam:(PushMsgInfo *)info;
	// 함수를 실행시킨다.
	if ( [self.delegate respondsToSelector:@selector(setPushInfo:withParam:)] ) {
		[self.delegate setPushInfo:self withParam:info];
	}
}

@end
