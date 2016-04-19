//
//  RegisterVC.h
//  pushbegins
//
//  Created by Deahyun Kim on 2016. 3. 14..
//  Copyright © 2016년 EnterTera. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RegisterVC : UIViewController

@property (weak, nonatomic) IBOutlet UISegmentedControl *seIdKind;
@property (weak, nonatomic) IBOutlet UITextField *tfDeviceId;

- (IBAction)onSelect:(id)sender;

- (IBAction)onSave:(id)sender;
- (IBAction)onCancel:(id)sender;

- (void)closeVC;
	
@end
