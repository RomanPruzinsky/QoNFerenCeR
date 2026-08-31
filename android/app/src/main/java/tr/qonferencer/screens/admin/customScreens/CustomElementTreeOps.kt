package tr.qonferencer.screens.admin.customScreens

import tr.qonferencer.shared.dtos.CustomElement

/** Children of [this], empty for a non-container element */
private val CustomElement.containerChildren: List<CustomElement>
	get() = when (this) {
		is CustomElement.Row -> children
		is CustomElement.Column -> children
		else -> emptyList()
	}

/** [this] with [children] replacing its own, unchanged for a non-container element */
private fun CustomElement.copyWithChildren(children: List<CustomElement>): CustomElement = when (this) {
	is CustomElement.Row -> copy(children = children)
	is CustomElement.Column -> copy(children = children)
	else -> this
}

/** [this] with element at [head] replaced by itself with [transform] applied to its children */
private fun List<CustomElement>.updatedAt(
	head: Int,
	transform: (List<CustomElement>) -> List<CustomElement>,
): List<CustomElement> {
	val target = this[head]
	return toMutableList().apply { set(head, target.copyWithChildren(transform(target.containerChildren))) }
}

/** Element nested at [path], chain of child indices from [this] */
fun List<CustomElement>.elementAt(path: List<Int>): CustomElement {
	val element = this[path.first()]
	return if (path.size == 1) element else element.containerChildren.elementAt(path.drop(1))
}

/** [this] with [element] inserted at [path], last index is target position within its container */
fun List<CustomElement>.insertedAt(
	path: List<Int>,
	element: CustomElement,
): List<CustomElement> {
	val head = path.first()
	if (path.size == 1) return toMutableList().apply { add(head, element) }

	val tail = path.drop(1)
	return updatedAt(head) { it.insertedAt(tail, element) }
}

/** [this] with element at [path] replaced by [element] */
fun List<CustomElement>.replacedAt(
	path: List<Int>,
	element: CustomElement,
): List<CustomElement> {
	val head = path.first()
	if (path.size == 1) return toMutableList().apply { set(head, element) }

	val tail = path.drop(1)
	return updatedAt(head) { it.replacedAt(tail, element) }
}

/** [this] with element at [path], and any of its children, removed */
fun List<CustomElement>.removedAt(path: List<Int>): List<CustomElement> {
	val head = path.first()
	if (path.size == 1) return toMutableList().apply { removeAt(head) }

	val tail = path.drop(1)
	return updatedAt(head) { it.removedAt(tail) }
}
