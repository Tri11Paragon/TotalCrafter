package com.brett.voxel.world.entity.animation.ColladaParser.colladaLoader;

import com.brett.voxel.world.entity.animation.ColladaParser.xmlParser.XmlNode;
import com.brett.voxel.world.entity.animation.ColladaParser.xmlParser.XmlParser;
import com.brett.voxel.world.entity.animation.dataStructures.AnimatedModelData;
import com.brett.voxel.world.entity.animation.dataStructures.AnimationData;
import com.brett.voxel.world.entity.animation.dataStructures.MeshData;
import com.brett.voxel.world.entity.animation.dataStructures.SkeletonData;
import com.brett.voxel.world.entity.animation.dataStructures.SkinningData;
import com.brett.voxel.world.entity.animation.utils.MyFile;

public class ColladaLoader {

	public static AnimatedModelData loadColladaModel(MyFile colladaFile, int maxWeights) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);

		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"), maxWeights);
		SkinningData skinningData = skinLoader.extractSkinData();

		SkeletonLoader jointsLoader = new SkeletonLoader(node.getChild("library_visual_scenes"), skinningData.jointOrder);
		SkeletonData jointsData = jointsLoader.extractBoneData();

		GeometryLoader g = new GeometryLoader(node.getChild("library_geometries"), skinningData.verticesSkinData);
		MeshData meshData = g.extractModelData();

		return new AnimatedModelData(meshData, jointsData);
	}

	public static AnimationData loadColladaAnimation(MyFile colladaFile) {
		XmlNode node = XmlParser.loadXmlFile(colladaFile);
		XmlNode animNode = node.getChild("library_animations");
		XmlNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode, jointsNode);
		AnimationData animData = loader.extractAnimation();
		return animData;
	}

}
