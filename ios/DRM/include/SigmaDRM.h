//
//  SigmaDRM.h
//  SigmaDRM
//
//  Created by NguyenVanSao on 12/21/17.
//  Copyright © 2017 NguyenVanSao. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVAssetResourceLoader.h>
#import <AVFoundation/AVAsset.h>
@protocol SigmaDRMDelegate
@optional
    -(void)onSigmaStatus:(NSInteger)status;
@end
@interface SigmaDRM : NSObject
{
    
}
@property(nonatomic, weak) id<SigmaDRMDelegate> delegate;
+(SigmaDRM *)getInstance;
-(AVURLAsset *)assetWithUrl:(NSString *)url;
-(AVURLAsset *)assset;
-(void)setClientId:(NSString *)clientId;
-(NSString *)clientId;
-(void)setAuthToken:(NSString *)token;
-(NSString *)authToken;
-(void)setSigmaUid:(NSString *)sigmaUid;
-(NSString *)sigmaUid;
-(void)setDrmUrl:(NSArray *)drmList;
-(NSArray *)drmList;
@end
