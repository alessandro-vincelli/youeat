//
//  YouEatAppDelegate.h
//  YouEat
//
//  Created by Alessandro Vincelli on 08/04/10.
//  Copyright __MyCompanyName__ 2010. All rights reserved.
//

@interface YouEatAppDelegate : NSObject <UIApplicationDelegate> {
    
    UIWindow *window;
    UINavigationController *navigationController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet UINavigationController *navigationController;

@end

