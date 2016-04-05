package nl.uu.mas.bdyj;

public class Item {
	public String name;
	public Color color;
	public Item(String name){
		this.name = name;
	}
	enum Color{
		Black,
		White,
		Green,
		Aqua
	}
	public Material material;
	enum Material{
		Gold,
		Copper,
		Wood,
		Silver
	}
	public int weight;
	public Item copy(){
		Item result = new Item(name);
		result.color = color;
		result.material = material;
		result.weight = weight;
		return result;
	}
}
