class Main {
  def main() : int {
    writeln(new helper().func());
    return 2;
  }
}

class test {
  def func1 () : int {
    var item : int;
    item = 2;
    item = this.func2();
    return item;
  }

  def func2() : int {
    return 3;
  }
}

class helper {
  var s : second;
  var h : helper;
  def func() : int {
    var status : int;
    s = new second();
    h = s.self();
    status = 0;
    status = h.test();

    return status;
  }

  def test() : int {
    return 3;
  }
}

class topper {
  def ok() : helper {
    return new helper();
  }

  def notOK() : int {
    return 4;
  }
}

class second extends topper {
  var c : int;
  var str : string;
  def func(a : int, b : string) : int {
    c = 2;
    str = b;
    writeln(str);
    return a + c;
  }

  def self() : helper {
    return this.ok();
  }

  def other() : int {
    return this.notOK();
  }
}