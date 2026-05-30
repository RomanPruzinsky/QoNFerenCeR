const { src, dest, task } = require('gulp');

task('build:icons', copyIcons);

function copyIcons() {
  const nodeSource = src('nodes/**/*.{png,svg}');
  const nodeDest   = nodeSource.pipe(dest('dist/nodes'));
  const credSource = src('credentials/**/*.{png,svg}');
  const credDest   = credSource.pipe(dest('dist/credentials'));
  return Promise.all([
    new Promise((r) => nodeDest.on('end', r)),
    new Promise((r) => credDest.on('end', r)),
  ]);
}
