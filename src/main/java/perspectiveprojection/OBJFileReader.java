package perspectiveprojection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import perspectiveprojection.linear_algebra.Point3D;
import perspectiveprojection.objects.Any3DObject;
import perspectiveprojection.primitives.Face;

public class OBJFileReader {
	public static Any3DObject readOBJ(String fileName, double size) {
		return readOBJ(new File(fileName), size);
	}
	
	public static Any3DObject readOBJ(File file, double size) {
		try (Scanner sc = new Scanner(file)) {
			List<Point3D> vertices = new ArrayList<>();
			List<Face> faces = new ArrayList<>();
			
			while (sc.hasNextLine()) {
				String s = sc.nextLine();
				if (s.isBlank() || s.startsWith("#")) {
					continue;
				}
				
				String[] parts = s.split(" ");
				
				switch (parts[0]) {
					case "v": //Normal vertex
						vertices.add(new Point3D(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3])));
						break;
					case "vt": //UV vertex (vertex texture)
						continue; //TODO: implement UV vertices
					case "vn": //Vertex normal
						continue; //TODO: implement vertex normals
					case "f": //Face
						Point3D[] vtxs = new Point3D[parts.length - 1];
						
						for (int i = 1; i < parts.length; i++) { //Usually 3 vertices per face, but this allows for more
							String[] v = parts[i].split("/"); //v[0] = vertex, v[1] = vertex texture, v[2] = vertex normal
							
							vtxs[i - 1] = vertices.get(Integer.parseInt(v[0]) - 1); //indexes in file start with 1, so minus 1.
						}
						
						faces.add(new Face(vtxs));
						break;
					case "g": //Group
						continue; //TODO: not sure if needed
					case "mtllib": //Where to find the materials (.mtl file name)
						continue; //TODO: not sure if needed
					case "usemtl": //Use material command
						continue; //TODO: not sure if needed
					default: //Other stuff
						continue;
				}
			}
			
			System.out.println("Vertices: " + vertices.size() + ", Faces: " + faces.size());
			return new Any3DObject(faces, size);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
		return null;
	}
}
