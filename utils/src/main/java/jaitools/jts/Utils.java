package jaitools.jts;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
/**
 * Helper class for utilities based on JTS classes 
 * @author Simone Giannecchini, GeoSolutions SAS
 *
 */
public class Utils {

	/** Geometry factory used to create LineStrings */
	public final static GeometryFactory GEOMETRY_FACTORY= new GeometryFactory(new PrecisionModel(100));

	private Utils() {
	}
	
	/**
	 * Removes collinear points from the provided linestring.
	 * 
	 * @param ls the {@link LineString} to be simplified.
	 * @return a new version of the provided {@link LineString} with collinear points removed.
	 */
	public static LineString removeCollinearVertices(final LineString ls){
		if(ls==null)
			throw new NullPointerException("The provided linestring is null");
	    final int N = ls.getNumPoints();
	    final boolean isLinearRing = ls instanceof LinearRing;
	    
	    List<Coordinate> retain = new ArrayList<Coordinate>();
	    retain.add(ls.getCoordinateN(0));
	    
	    int i0 = 0, i1 = 1, i2 = 2;
	    Coordinate firstCoord=ls.getCoordinateN(i0);
	    Coordinate midCoord;
	    Coordinate lastCoord;
	    while (i2 < N) {

	    	midCoord=ls.getCoordinateN(i1);
	    	lastCoord=ls.getCoordinateN(i2);
	        final int orientation = CGAlgorithms.computeOrientation(
	        		firstCoord,midCoord, lastCoord);
	        // Colllinearity test
	        if (orientation != CGAlgorithms.COLLINEAR) {
	        	// add midcoord and change head
	            retain.add(midCoord);
	            i0=i1;
	            firstCoord=ls.getCoordinateN(i0);
	        }
	        i1++; i2++;	    	
	    }
	    retain.add(ls.getCoordinateN(N-1));
	    

	    //
	    // Return value
	    //
	    final int size=retain.size();
	    // nothing changed?
	    if(size==N){
	    	// free everything and return original
	    	retain.clear();
	    	return ls;
	    }
	    return isLinearRing?
	    		ls.getFactory().createLinearRing(retain.toArray(new Coordinate[size])):
	    			ls.getFactory().createLineString(retain.toArray(new Coordinate[size]));
	}
	
	/**
	 * Removes collinear vertices from the provided {@link Polygon}.
	 * @param polygon the instance of a {@link Polygon} to remove collinear vertices from.
	 * @return a new instance of the provided  {@link Polygon} without collinear vertices.
	 */
	public static Polygon removeCollinearVertices(final Polygon polygon) {
		if(polygon==null)
			throw new NullPointerException("The provided Polygon is null");	
		// reuse existing factory		
        final GeometryFactory gf = polygon.getFactory();

        // work on the exterior ring
        LineString exterior = polygon.getExteriorRing();
        LineString shell = removeCollinearVertices(exterior);
        if(shell == null || shell.isEmpty()) {
            return null;
        }

        // work on the holes
        List<LineString> holes = new ArrayList<LineString>();
        final int size=polygon.getNumInteriorRing();
        for (int i = 0; i <size; i++) {
        	LineString hole = polygon.getInteriorRingN(i);
            hole = removeCollinearVertices(hole);
            if(hole != null && !hole.isEmpty()) {
                holes.add(hole);
            }
        }

        return gf.createPolygon((LinearRing)shell, (LinearRing[]) holes.toArray(new LinearRing[holes.size()]));
	}
	
	/**
	 * Removes collinear vertices from the provided {@link Geometry}.
	 * 
	 * <p>
	 * For the moment this implementation only accepts, {@link Polygon}, {@link LinearRing} and {@link LineString}.
	 * It return <code>null</code> in case the geometry is not of these types. 
	 * 
	 * @todo implement submethods for {@link GeometryCollection} sublcaases.
	 * @param g the instance of a {@link Geometry} to remove collinear vertices from.
	 * @return a new instance of the provided  {@link Geometry} without collinear vertices.
	 */
	public static Geometry removeCollinearVertices(final Geometry g) {
		if(g==null)
			throw new NullPointerException("The provided Geometry is null");		
		if(g instanceof LineString)
			return removeCollinearVertices((LineString)g);
		if(g instanceof Polygon)
			return removeCollinearVertices((Polygon)g);			
			 
		throw new IllegalArgumentException("This method can work on LineString and Polygon.");
	}

}