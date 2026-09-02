package tr.qonferencer.screens.admin.translations

/**
 * Node of a `.`-segmented translation-key tree
 * @property name This node's own segment
 * @property path Full dot-joined path from root to this node
 */
sealed class TranslationTreeNode {
	abstract val name: String
	abstract val path: String

	/** Non-leaf segment grouping [children] */
	data class Category(
		override val name: String,
		override val path: String,
		val children: List<TranslationTreeNode>,
	) : TranslationTreeNode()

	/** Leaf holding one full translation key ([path]) */
	data class Leaf(
		override val name: String,
		override val path: String,
	) : TranslationTreeNode()
}

/** Number of [TranslationTreeNode.Leaf]s under [this], recursively */
fun TranslationTreeNode.leafCount(): Int = when (this) {
	is TranslationTreeNode.Leaf -> 1
	is TranslationTreeNode.Category -> children.sumOf { it.leafCount() }
}

/** Builds a `.`-segmented tree from [keys], sorted alphabetically at every level */
fun buildTranslationTree(keys: Collection<String>): List<TranslationTreeNode> = buildNodes(keys.toList(), prefix = "")

private fun buildNodes(
	keys: List<String>,
	prefix: String,
): List<TranslationTreeNode> = keys
	.groupBy { it.substringBefore('.') }
	.toSortedMap()
	.map { (segment, groupKeys) ->
		val path =
			if (prefix.isEmpty()) segment
			else "$prefix.$segment"
		val isAlsoOwnKey = segment in groupKeys
		val childKeys = groupKeys.mapNotNull { if (it == segment) null else it.removePrefix("$segment.") }

		if (childKeys.isEmpty()) {
			TranslationTreeNode.Leaf(name = segment, path = path)
		} else {
			val nested = buildNodes(childKeys, path)
			val children =
				if (isAlsoOwnKey) listOf(TranslationTreeNode.Leaf(segment, path)) + nested
				else nested
			TranslationTreeNode.Category(name = segment, path = path, children = children)
		}
	}
