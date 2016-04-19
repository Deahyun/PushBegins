//
//  PushInfoCell.h
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 17..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PushInfoCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UILabel *time;
@property (weak, nonatomic) IBOutlet UILabel *alert;
@property (weak, nonatomic) IBOutlet UILabel *message;

@end
