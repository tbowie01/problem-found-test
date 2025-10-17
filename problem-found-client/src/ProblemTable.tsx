import {
  createColumnHelper,
  getCoreRowModel,
  useReactTable,
  flexRender,
} from "@tanstack/react-table";
import { useEffect, useState } from "react";

type Problem = {
  source: string;
  text: string;
  url: string;
  date: Date;
};

type ProblemDTO = {
  externalId: string;
  source: string;
  text: string;
  url: string;
  sourceCreated: string;
  keywords: string[];
};

type PaginationState = {
  pageIndex: number;
  pageSize: number;
};

type ResultsInfo = {
  pageCount: number;
  rowCount: number;
};

// const testData = [
//   {
//     source: "reddit",
//     text: "I have a bad problem",
//     url: "www.test.com",
//     date: new Date(),
//   },
//   {
//     source: "reddit",
//     text: "I wish that this would exist",
//     url: "www.test2.com",
//     date: new Date(),
//   },
// ];

const columnHelper = createColumnHelper<Problem>();

const columns = [
  columnHelper.accessor((row) => row.text, {
    id: "text",
    header: () => "Problem",
    cell: (info) => info.getValue(),
  }),
  columnHelper.accessor((row) => row.source, {
    id: "source",
    header: () => "Source",
    cell: (info) => info.getValue(),
  }),
  columnHelper.accessor((row) => row.url, {
    id: "url",
    header: () => "Original URL",
    cell: (info) => (
      <a href={info.getValue()} target="_blank">
        link
      </a>
    ),
  }),
  columnHelper.accessor((row) => row.date, {
    id: "date",
    header: () => "Posted Date",
    cell: (info) => info.getValue().toDateString(),
  }),
];

export default function ProblemTable() {
  const [data, setData] = useState<Problem[]>([]);

  const [pagination, setPagination] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 10,
  });

  useEffect(() => {
    const params = new URLSearchParams({
      pageIndex: pagination.pageIndex.toString(),
      size: pagination.pageSize.toString(),
    });
    fetch(`http://localhost:8080/api/problems?${params}`)
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP Error Status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        const content = data.content as ProblemDTO[];
        const problems = content.map((contentObj) => {
          const problem: Problem = {
            source: contentObj.source,
            text: contentObj.text,
            url: contentObj.url,
            date: new Date(contentObj.sourceCreated),
          };
          return problem;
        });
        setResultsInfo({
          pageCount: data.totalPages,
          rowCount: data.totalElements,
        });
        setData(problems);
      })
      .catch((error) => {
        console.log(error);
      });
  }, [pagination]);

  const [resultsInfo, setResultsInfo] = useState<ResultsInfo>({
    pageCount: 0,
    rowCount: 0,
  });

  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
    onPaginationChange: setPagination,
    manualPagination: true,
    pageCount: resultsInfo.pageCount,
    state: {
      pagination,
    },
  });

  return (
    <div className="p-5">
      <table>
        <thead>
          {table.getHeaderGroups().map((headerGroup) => (
            <tr key={headerGroup.id}>
              {headerGroup.headers.map((header) => (
                <th
                  key={header.id}
                  className="px-2 bg-gray-100 border-gray-200 border-2 whitespace-nowrap"
                >
                  {header.isPlaceholder
                    ? null
                    : flexRender(
                        header.column.columnDef.header,
                        header.getContext()
                      )}
                </th>
              ))}
            </tr>
          ))}
        </thead>
        <tbody>
          {table.getRowModel().rows.map((row) => (
            <tr key={row.id}>
              {row.getVisibleCells().map((cell) => (
                <td key={cell.id} className="border-2 border-gray-200">
                  {flexRender(cell.column.columnDef.cell, cell.getContext())}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
      <div className="flex items-center gap-2">
        <button
          className="border rounded p-1"
          onClick={() => table.firstPage()}
          disabled={!table.getCanPreviousPage()}
        >
          {"<<"}
        </button>
        <button
          className="border rounded p-1"
          onClick={() => table.previousPage()}
          disabled={!table.getCanPreviousPage()}
        >
          {"<"}
        </button>
        <button
          className="border rounded p-1"
          onClick={() => table.nextPage()}
          disabled={!table.getCanNextPage()}
        >
          {">"}
        </button>
        <button
          className="border rounded p-1"
          onClick={() => table.lastPage()}
          disabled={!table.getCanNextPage()}
        >
          {">>"}
        </button>
        <span className="flex items-center gap-1">
          <div>Page</div>
          <strong>
            {table.getState().pagination.pageIndex + 1} of{" "}
            {table.getPageCount().toLocaleString()}
          </strong>
        </span>
        <span className="flex items-center gap-1">
          | Go to page:
          <input
            type="number"
            min="1"
            max={table.getPageCount()}
            defaultValue={table.getState().pagination.pageIndex + 1}
            onChange={(e) => {
              const page = e.target.value ? Number(e.target.value) - 1 : 0;
              table.setPageIndex(page);
            }}
            className="border p-1 rounded w-16"
          />
        </span>
        <select
          value={table.getState().pagination.pageSize}
          onChange={(e) => {
            table.setPageSize(Number(e.target.value));
          }}
        >
          {[10, 20, 30, 40, 50].map((pageSize) => (
            <option key={pageSize} value={pageSize}>
              Show {pageSize}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
}
