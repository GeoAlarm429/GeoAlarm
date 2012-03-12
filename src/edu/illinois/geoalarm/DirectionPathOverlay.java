package edu.illinois.geoalarm;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class DirectionPathOverlay extends Overlay {
	 
    private GeoPoint gp1; 
    private GeoPoint gp2; 
 
    public DirectionPathOverlay(GeoPoint gp1, GeoPoint gp2) { 
        this.gp1 = gp1; 
        this.gp2 = gp2; 
    } 
 
    @Override 
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) { 
        Projection projection = mapView.getProjection(); 
        if (shadow == false) { 
 
            Paint paint = new Paint(); 
            paint.setAntiAlias(true); 
            
            Point point = new Point(); 
            projection.toPixels(gp1, point); 
            paint.setColor(Color.BLUE); 
            
            Point point2 = new Point(); 
            projection.toPixels(gp2, point2);
            
			paint.setStrokeWidth(10); 
			paint.setAlpha(120); 
            
            canvas.drawLine((float) point.x, (float) point.y, (float) point2.x, (float) point2.y, paint); 
        } 
        
        return super.draw(canvas, mapView, shadow, when); 
    } 
 
    @Override 
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow); 
    }
}
