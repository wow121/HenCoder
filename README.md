# HenCoder
![](http://ovlhd8vdh.bkt.gdipper.com/hencoder.gif)

```
    trimmingView.setEnableLoop(true) //设置是否循环
    
    trimmingView.setValueRange(max) //设置值范围
    
    trimmingView.setValue() //设置游标位置
    
    trimmingView.setOnValueChangeListener() //设置回调
         void onScroll(float distance);
    
         void onProgressChanged(TrimmingView trimmingView, float progress, boolean fromUser);
    
         void onStartTrackingTouch(TrimmingView trimmingView);
    
         void onStopTrackingTouch(TrimmingView trimmingView);
    
    trimmingView.setTickTextAdapter() //自定义高亮文字adapter
         String getTickText(int index) 
    
```