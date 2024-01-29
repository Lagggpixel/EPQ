import os
import time
import tkinter as tk
import numpy as np
import pandas as pd
import math

from matplotlib.animation import FuncAnimation
from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg, NavigationToolbar2Tk
from matplotlib.figure import Figure

SPREADSHEET_PATH = os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))), "Databook.xlsx")
MARKER_SIZE = 2

length_of_pendulum = 600


def filter_numeric_values(lst):
    new_list = []
    for x in lst:
        if isinstance(x, (float, int)):
            new_list.append(x)
        else:
            new_list.append(0)
    return new_list


def calculate_y_values(x_values, z_values):
    lst = []
    temp = length_of_pendulum ** 2
    for (x, z) in zip(x_values, z_values):
        lst.append(math.sqrt(temp - (x ** 2) - (z ** 2)))
    return lst


class Simulator:
    def __init__(self, window):
        self.root = window
        self.spreadsheet = pd.read_excel(SPREADSHEET_PATH, sheet_name="Trial 3")
        print(self.spreadsheet)
        self.x_values = np.array(filter_numeric_values(self.spreadsheet["True x position / unit"].tolist()))
        self.z_values = np.array(filter_numeric_values(self.spreadsheet["True y position / unit"].tolist()))
        self.y_values = np.array(calculate_y_values(x_values=self.x_values, z_values=self.z_values))
        self.counter = 0

        self.root.title("Simulation")
        self.root.configure(background='white')
        self.root.resizable(False, False)
        self.fig = Figure(figsize=(7, 7), dpi=90)
        self.axis = self.fig.add_subplot(111, projection='3d')
        self.axis.set(xlabel='X', ylabel='Y', zlabel='Z', title='3d Simulation')
        self.axis.set_xlim(-500, 500)
        self.axis.set_ylim(-500, 500)
        self.axis.set_aspect('equal', adjustable='datalim')

        self.canvas = FigureCanvasTkAgg(self.fig, master=self.root)
        self.toolbar = NavigationToolbar2Tk(self.canvas, self.root)

        self.allPlottedPoints = []

    def start(self):
        # self.animation = FuncAnimation(self.fig, self.animate, interval=0, blit=True, cache_frame_data=False)

        self.canvas.draw()
        # self.toolbar.update()
        self.toolbar.pack()
        self.canvas.get_tk_widget().pack()

    def animate(self, i) -> list:
        self.allPlottedPoints = []
        for i in range(self.counter, self.counter + 10):
            self.allPlottedPoints.append(self.axis.plot(self.x_values[i], self.y_values[i], self.z_values[i], "o",
                                                        markersize=MARKER_SIZE, markeredgecolor="red",
                                                        markerfacecolor="red")[0])

        self.counter += 1

        time.sleep(0.01)

        return self.allPlottedPoints


if __name__ == "__main__":

    root = tk.Tk()

    sim = Simulator(root)
    sim.start()

    root.mainloop()

