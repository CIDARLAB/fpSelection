# fpSelection

Requires Java 8

## Dependencies

### gnuplot

Install gd for image (png, jpeg, gif) support:

**apt package manager**
```
sudo apt install libgd-dev
```

**yum package manager**
```
sudo yum install gd-devel
```

Compile gnuplot from source to capture dependencies:
- https://sourceforge.net/projects/gnuplot/

Compile & install gnuplot (gnuplot 5.2.8 used in this example)

```
cd path/to/your/gnuplot-5.2.8.tar.gz
tar -xvf gnuplot-5.2.8.tar.gz
cd gnuplot-5.2.8
./configure
sudo make install
```

### maven

```
sudo apt install maven
```

## Install

### Package fpSelection
```
cd path/to/fpSelection/fpSelection

```
