const { src, dest, task } = require('gulp');

task('build:icons', copyIcons);

function copyIcons() {
	const nodeSource = src('nodes/**/*.{png,svg,json}');
	const nodeDest = nodeSource.pipe(dest('dist/nodes'));
	const credSource = src('credentials/**/*.{png,svg,json}');
	const credDest = credSource.pipe(dest('dist/credentials'));
	return Promise.all([
		new Promise((resolve, reject) => nodeDest.on('finish', resolve).on('error', reject)),
		new Promise((resolve, reject) => credDest.on('finish', resolve).on('error', reject)),
	]);
}
