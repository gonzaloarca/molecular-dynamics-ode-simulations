import numpy as np
from matplotlib import pyplot as plt


def plot_methods(total_time, steps: int, results: list[list], methods: list[str]):
    x = np.linspace(0, total_time, steps)

    for result in results:
        plt.plot(x, result)

    plt.legend(methods)
    plt.ylim(top=2, bottom=-2)
    plt.show()


if __name__ == '__main__':
    results = []
    with open('../output.csv', 'r') as f:
        for line_number, line in enumerate(f):
            if line_number == 0:
                methods = line.split(";")
                for method in methods:
                    results.append([])
            else:
                output = line.split(";")
                for index, method in enumerate(methods):
                    results[index].append(float(output[index]))

    plot_methods(5, 10000, results, methods)
