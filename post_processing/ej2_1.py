import os
from statistics import mean, stdev
from matplotlib import pyplot as plt
import numpy as np

from ej2_utils.ej2_utils import parse_simulation_output


def run_ej2_1_simulations(dts: list[float], initial_heights: list[float]):
    energies_list = []

    for dt in dts:

        energies = []

        for initial_height in initial_heights:
            cmd = f"java -DstepSize={dt} -DinitialHeightRatio={initial_height} -DinitialSpeed={5e4} -jar ./target/molecular-dynamics-ode-simulations-1.0-SNAPSHOT.jar"

            print(f"Executing: {cmd}")
            os.system(cmd)
            print("Done")

            energy = parse_simulation_output()

            energies.append(energy)

        energies_list.append(energies)

    return energies_list


def get_ej2_1_data(energies_list: list[list[dict]], dts: list[float]):
    avg_energies = []
    stdev_energies = []

    for energies in energies_list:

        relative_differences = []

        for energy in energies:
            relative_differences.append(abs(energy["initial"] -
                                            energy["final"]) / energy["initial"])

        avg_energies.append(mean(relative_differences))
        stdev_energies.append(stdev(relative_differences))

    return avg_energies, stdev_energies


def plot_ej2_1(avg_energies: list[float], stdev_energies: list[float], dts: list[float]):
    plt.errorbar(dts, avg_energies, yerr=stdev_energies,
                 ls="none", ecolor="blue", marker="o", color="red", elinewidth=0.5, capsize=5)
    plt.xlabel("Paso de integración (s)", fontsize=20)
    plt.ylabel("Diferencia relativa promedio", fontsize=20)
    plt.xticks(fontsize=16)
    plt.yticks(fontsize=16)

    # plt.xscale("log")
    # plt.yscale("log")

    plt.show()


def save_plot_data_to_file(avg_energies: list[float], stdev_energies: list[float], dts: list[float]):
    with open("./output_files/ej2_1_data.csv", "w") as f:
        f.write("dt;avg_energy;stdev_energy\n")
        for index, dt in enumerate(dts):
            f.write(f"{dt};{avg_energies[index]};{stdev_energies[index]}\n")


if __name__ == "__main__":
    # dts = np.logspace(-19, -14, 7)
    dts = [1e-14, 5e-15, 1e-15, 5e-16, 1e-16, 5e-17, 1e-17]
    initial_heights = np.linspace(0, 1, 5)

    energies_list = run_ej2_1_simulations(
        dts, initial_heights)

    avg_energies, stdev_energies = get_ej2_1_data(energies_list, dts)

    save_plot_data_to_file(avg_energies, stdev_energies, dts)
    plot_ej2_1(avg_energies, stdev_energies, dts)
