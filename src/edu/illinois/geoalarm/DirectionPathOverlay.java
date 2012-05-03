package edu.illinois.geoalarm;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * This class implements Android Overlay to allow display of travel paths
 * on the map
 * @author GeoAlarm
 *
 */

public class DirectionPathOverlay extends Overlay 
{
	 
    private GeoPoint startPoint; 
    private GeoPoint endPoint; 
 
    /**
     * Constructs a new DirectionPathOverlay, with the specified points
     * @param startPoint - The start point of the path to be drawn
     * @param endPoint - The end point of the path to be drawn
     */
    public DirectionPathOverlay(GeoPoint startPoint, GeoPoint endPoint) 
    { 
        this.startPoint = startPoint; 
        this.endPoint = endPoint; 
    } 
 
    @Override 
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) 
    { 
        Projection projection = mapView.getProjection(); 
        if (shadow == false) { 
 
            Paint paint = new Paint(); 
            paint.setAntiAlias(true); 
            
            Point point = new Point(); 
            projection.toPixels(startPoint, point); 
            paint.setColor(Color.BLUE); 
            
            Point point2 = new Point(); 
            projection.toPixels(endPoint, point2);
            
			paint.setStrokeWidth(10); 
			paint.setAlpha(120); 
            
            canvas.drawLine((float) point.x, (float) point.y, (float) point2.x, (float) point2.y, paint); 
        } 
        
        return super.draw(canvas, mapView, shadow, when); 
    } 
 
    @Override 
    public void draw(Canvas canvas, MapView mapView, boolean shadow)
    {
        super.draw(canvas, mapView, shadow); 
    }
}
